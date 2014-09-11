package com.aashithkamathm.android.sunshine;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;

/**
 * Created by aashith on 8/8/2014.
 */
public class fullTestSuite {

    public static Test suite()
    {
        return new TestSuiteBuilder(fullTestSuite.class).includeAllPackagesUnderHere().build();
    }

    public fullTestSuite() {
        super();
    }
}
