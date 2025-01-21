#!/bin/bash

# Realiza o deploy da aplicação no cluster EKS
# Este script simula os passos realizados no workflow de CD

EKS_CLUSTER_NAME=app-cluster
API_GATEWAY_NAME=http-api
COGNITO_USER_POOL=customer-logins

APP_IMAGE=dockerhubalancarvalho/fiap-videoslice-svc-engine:0.0.8


TMP_OUTPUTS=/tmp/deploy/tmp_outputs.vars

baseDir=$(dirname $0)

cd $baseDir || exit 1
mkdir /tmp/deploy


./utils/check-cluster-status.sh ${EKS_CLUSTER_NAME}
if [ $? -ne 0 ]
then
  echo "Error checking AWS resource. See error messages"
  exit 1
fi

source $TMP_OUTPUTS

./utils/get-external-api-info.sh ${API_GATEWAY_NAME} > $TMP_OUTPUTS
if [ $? -ne 0 ]
then
  echo "Error checking AWS resource. See error messages"
  exit 1
fi
source $TMP_OUTPUTS

./utils/get-user-pool-info.sh ${COGNITO_USER_POOL} > $TMP_OUTPUTS
if [ $? -ne 0 ]
then
  echo "Error checking AWS resource. See error messages"
  exit 1
fi
source $TMP_OUTPUTS


cat ./k8s/app/aws-configs-template.yml \
   | sed "s/{ACCESS_KEY_ID}/$(echo -n ${aws_access_key_id} | tr -d '\n')/" \
   | sed "s|{SECRET_ACCESS_KEY}|$(echo -n ${aws_secret_access_key} | tr -d '\n')|" \
   | sed "s|{SESSION_TOKEN}|$(echo -n ${aws_session_token} | tr -d '\n')|" \
   | sed "s/{REGION}/us-east-1/" \
   | sed "s/{USER_POOL_ID}/${USER_POOL_ID}/" \
   | sed "s/{USER_POOL_CLIENT_ID}/${USER_POOL_CLIENT_ID}/" \
   | sed "s/{USER_POOL_CLIENT_SECRET}/${USER_POOL_CLIENT_SECRET}/" > /tmp/deploy/aws-configs.yml

if [ $? -ne 0 ]
then
  echo "Error preparing configmap. See error messages"
  exit 1
fi

cat ./k8s/app/app-deployment.yml \
  | sed "s|image: .*architect-burgers-pagamentos.*$|image: $APP_IMAGE|" > /tmp/deploy/app-deployment.yml

aws eks update-kubeconfig --name ${EKS_CLUSTER_NAME}


kubectl apply -f /tmp/deploy/aws-configs.yml
if [ $? -ne 0 ]
then
  echo "Error running kubectl step. See error messages"
  exit 1
fi

kubectl apply -f ./k8s/app/app-service-loadbalancer.yml
if [ $? -ne 0 ]
then
  echo "Error running kubectl step. See error messages"
  exit 1
fi

kubectl apply -f /tmp/deploy/app-deployment.yml
if [ $? -ne 0 ]
then
  echo "Error running kubectl step. See error messages"
  exit 1
fi

kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
if [ $? -ne 0 ]
then
  echo "Error running kubectl step. See error messages"
  exit 1
fi

kubectl apply -f ./k8s/app/app-hpa.yml
if [ $? -ne 0 ]
then
  echo "Error running kubectl step. See error messages"
  exit 1
fi
