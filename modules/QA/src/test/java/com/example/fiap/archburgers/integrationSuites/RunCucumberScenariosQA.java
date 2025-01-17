package com.example.fiap.archburgers.integrationSuites;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SuiteDisplayName("BDD Scenarios")
@SelectPackages("scenarios")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
public class RunCucumberScenariosQA {

}
