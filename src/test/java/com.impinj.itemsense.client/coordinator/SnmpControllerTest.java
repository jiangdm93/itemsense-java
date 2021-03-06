/*
 * IMPINJ CONFIDENTIAL AND PROPRIETARY
 *
 * This source code is the sole property of Impinj, Inc. Reproduction or
 * utilization of this source code in whole or in part is forbidden without
 * the prior written consent of Impinj, Inc.
 *
 * (c) Copyright Impinj, Inc. 2016. All rights reserved.
 */

package com.impinj.itemsense.client.coordinator;

import com.google.gson.Gson;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.impinj.itemsense.client.TestUtils;
import com.impinj.itemsense.client.coordinator.readerdefintion.ReaderDefinitionController;
import com.impinj.itemsense.client.coordinator.settings.snmp.SnmpAuthConfiguration;
import com.impinj.itemsense.client.coordinator.settings.snmp.SnmpCommunityAuthConfiguration;
import com.impinj.itemsense.client.coordinator.settings.snmp.SnmpConfiguration;
import com.impinj.itemsense.client.coordinator.settings.snmp.SnmpController;
import com.impinj.itemsense.client.coordinator.settings.snmp.SnmpTrapTargetConfiguration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;

public class SnmpControllerTest {

    private CoordinatorApiController coordinatorApiController;
    private SnmpController snmpController;
    private Gson gson;

    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(TestUtils.MOCK_PORT);

    @Rule
    public WireMockClassRule instanceRule = wireMockRule;


    @Before
    public void setUp() throws Exception {

        Client client = TestUtils.getClient();

        coordinatorApiController = new CoordinatorApiController(client, TestUtils.MOCK_URI);
        snmpController = coordinatorApiController.getSnmpController();
        gson = new Gson();
    }


    @Test
    public void testGetSnmpConfiguration() {
        SnmpCommunityAuthConfiguration authConfig = new SnmpCommunityAuthConfiguration();
        authConfig.setType(SnmpAuthConfiguration.AuthType.V2_COMMUNITY);
        authConfig.setCommunityName("foo");

        SnmpTrapTargetConfiguration snmpTrapConfig = new SnmpTrapTargetConfiguration();
        snmpTrapConfig.setHost("127.0.0.1");

        SnmpConfiguration snmpConfig = new SnmpConfiguration();
        snmpConfig.setAuthConfig(authConfig);
        snmpConfig.setTrapTargetConfig(snmpTrapConfig);

        stubFor(get(urlEqualTo("/configuration/v1/settings/SNMP")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(gson.toJson(snmpConfig))));

        SnmpConfiguration returnedConfig = snmpController.getSnmpConfiguration();

        assertEquals(authConfig.getType(), returnedConfig.getAuthConfig().getType());
        assertEquals(authConfig.getCommunityName(), ((SnmpCommunityAuthConfiguration)returnedConfig.getAuthConfig()).getCommunityName());
        assertEquals(snmpTrapConfig.getHost(), returnedConfig.getTrapTargetConfig().getHost());
    }

    @Test
    public void testUpdateSnmpConfiguration() {
        SnmpCommunityAuthConfiguration authConfig = new SnmpCommunityAuthConfiguration();
        authConfig.setType(SnmpAuthConfiguration.AuthType.V2_COMMUNITY);
        authConfig.setCommunityName("foo");

        SnmpTrapTargetConfiguration snmpTrapConfig = new SnmpTrapTargetConfiguration();
        snmpTrapConfig.setHost("127.0.0.1");

        SnmpConfiguration snmpConfig = new SnmpConfiguration();
        snmpConfig.setAuthConfig(authConfig);
        snmpConfig.setTrapTargetConfig(snmpTrapConfig);

        stubFor(put(urlEqualTo("/configuration/v1/settings/SNMP")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(gson.toJson(snmpConfig))));

        Response response = snmpController.updateSnmpConfigurationAsResponse(snmpConfig);
        Assert.assertEquals(200, response.getStatus());
        response.close();

        SnmpConfiguration returnedConfig = snmpController.updateSnmpConfiguration(snmpConfig);
        Assert.assertEquals(snmpConfig, returnedConfig);
    }
}


