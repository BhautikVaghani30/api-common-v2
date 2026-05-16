/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.config;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import com.launchdarkly.sdk.ContextBuilder;
import com.launchdarkly.sdk.LDValue;
import com.ulta.dsp.common.constants.UltaCommonConstants;
import io.split.client.SplitClient;
import io.split.client.SplitClientConfig;
import io.split.client.SplitFactory;
import io.split.client.SplitFactoryBuilder;
import io.split.client.api.Key;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import com.launchdarkly.sdk.LDContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.launchdarkly.sdk.server.Components;
import com.launchdarkly.sdk.server.LDClient;
import com.launchdarkly.sdk.server.LDConfig;
import com.launchdarkly.sdk.server.integrations.FileData;

import lombok.extern.slf4j.Slf4j;

/**
 * This Launch Darkly configuration can be used to check the feature flag 
 * defined on Launch Darkly, this class will initialize Launch Darkly client.
 * 
 * Add these 2 properties in application properties:
 *    1. launch.darkly.client.key
 *    2. launch.darkly.client.user
 * 
 * @Usage
 * Autowire this config using Spring and then call the flag check method 
 * as shown below:
 * <pre>
 * if(launchDarklyConfig.flagEnabled("pdpPreview")) {
 *   	newMethod();
 *   } else {
 *   	oldMethod();
 *   }
 * </pre> 
 * 
 * @author mmisbahuddin
 *
 */
@Slf4j
@Configuration
public class LaunchDarklyConfig {

    @Value("${launch.darkly.client.key:}")
    private String ldClientKey;

    @Value("${launch.darkly.client.user:}")
    private String ldClientUser;

    @Value("${launch.darkly.relay.proxy:}")
    private String ldRelayProxy;

    @Value("${launch.darkly.test.flags.json:}")
    private String testJsonFile;

    private LDClient ldClient;

    @Value("${CLUSTER_ID:Unknown}")
    private String clusterName;

    @Value("${POD_NAMESPACE:Unknown}")
    private String namespace;

    @Value("${harness.client.key:}")
    private String harnessClientKey;

    @Value("${harness.client.user:}")
    private String harnessClientUser;

    @Value("${harness.relay.proxy:}")
    private String harnessRelayProxy;

    private SplitClient splitClient;

    @Value("${harness.client.enableHarnessFME:false}")
    public boolean enableHarnessFME;


    @PostConstruct
    public void postConstruct() {
      if( enableHarnessFME && StringUtils.isNotBlank(harnessClientKey)){
          try{
              invokeHarnessClient();
          }catch (Exception e) {
              log.error("Exception occurred while connecting Harness.");
          }
      }
          invokeLaunchDarkly();
  }

    /**
     *  This method is used to call harness.
     * @throws IOException
     * @throws URISyntaxException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    private void invokeHarnessClient() throws IOException, URISyntaxException, TimeoutException, InterruptedException {
        log.debug("Harness client configured for relay URI: {}", harnessRelayProxy);
        SplitClientConfig.Builder configBuilder = SplitClientConfig.builder()
                .setBlockUntilReadyTimeout(10000);

        if (StringUtils.isNotBlank(harnessRelayProxy)) {
            configBuilder.endpoint(harnessRelayProxy, harnessRelayProxy)
            .authServiceURL(harnessRelayProxy)
            .telemetryURL(harnessRelayProxy);
            log.debug("Harness client configured for relay URI: {}", harnessRelayProxy);
        }
        SplitClientConfig config = configBuilder.build();
        SplitFactory splitFactory = SplitFactoryBuilder.build(harnessClientKey, config);
        splitClient = splitFactory.client();
        splitClient.blockUntilReady();
        log.info("LaunchDarklyConfig: invokeHarnessClient – Harness client proxy initialization has been completed : {}", harnessClientKey);
    }

    /**
     *  This method is used to initialize the launchdarkly client.
     */
    private void invokeLaunchDarkly() {
        if (StringUtils.isNotBlank(ldClientKey)) {
            URI relayUri = URI.create(ldRelayProxy);
            LDConfig config =
                    new LDConfig.Builder().serviceEndpoints(Components.serviceEndpoints().streaming(relayUri).polling(relayUri))
                            .events(Components.noEvents()).startWait(Duration.ZERO)
                            .build();
            ldClient = new LDClient(ldClientKey, config);
            log.info("LDClient init to URI {} completed", ldRelayProxy);
        } else {
            if (StringUtils.isNotBlank(testJsonFile)) {
                // There is a local config for the feature flags, to be used during unit testing
                LDConfig config =
                        new LDConfig.Builder()
                                .dataSource(
                                        FileData.dataSource().filePaths(testJsonFile.split(",")))
                                .events(Components.noEvents()).startWait(Duration.ZERO)
                                .build();
                ldClient = new LDClient("unitTestKey", config);
                ldClient.isInitialized();
            }
        }
    }

    @PreDestroy
    public void preDestroy() {
        if (ldClient != null) {
            try {
                ldClient.close();
            } catch (IOException ex) {
                log.error("IO Exception closing LauchDarkly Client", ex);
            }
        }
    }


    /**
     * Make a call to launch darkly to see if this rollout is on or off
     * @param flag
     * @return
     */
    public boolean flagEnabled(String flag) {
        if(enableHarnessFME && StringUtils.isNotBlank(harnessClientUser)){
            try{
                return fetchFlagValueFromHarness(flag);
            }catch(Exception e){
                log.error("Exception occured while fetching flag value from Harness :   Exception is : {}",e);
                return fetchFlagValueFromLaunchDarkly(flag);
            }
        }else{
     return fetchFlagValueFromLaunchDarkly(flag);
    }
    }

    /**
     * Fetch flag value from Harness.
     * @param flag
     * @return
     */
    private boolean fetchFlagValueFromHarness(String flag) {
        log.debug(
                "Harness : fetchFlagValueFromHarness :: Flag :{} ,namespace : {} , clusterName : {}",
                flag,
                namespace,
                clusterName);
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(UltaCommonConstants.NAMESPACE, namespace);
        attributes.put(UltaCommonConstants.CLUSTER, clusterName);
        String treatment = splitClient.getTreatment(harnessClientUser, flag, attributes);
        if (StringUtils.isNotBlank(treatment) && treatment.equals(UltaCommonConstants.ON)) {
            log.debug("LaunchDarklyConfig : flagEnabled : fetchFlagValueFromHarness : Flag Enabled :: Rollout {} is {}", flag, treatment);
            return true;
        } else if (StringUtils.isNotBlank(treatment) && treatment.equals(UltaCommonConstants.OFF)) {
            log.debug("LaunchDarklyConfig : flagEnabled : fetchFlagValueFromHarness : Flag Disabled ::  Rollout {} is {}", flag, treatment);
            return false;
        } else if (StringUtils.isNotBlank(treatment) && treatment.equals(UltaCommonConstants.CONTROL)) {
            //If the flag is not configured in Harness, it will fall back to LD.
            log.debug("LaunchDarklyConfig : flagEnabled : fetchFlagValueFromHarness : Received treatment as control ::  Rollout {} is {}", flag, treatment);
            return fetchFlagValueFromLaunchDarkly(flag);
        }else {
            return false;
        }
    }

    /**
     *  This method is used to fetch the flag from launchdarkly.
     * @param flag
     * @return
     */
    private boolean fetchFlagValueFromLaunchDarkly(String flag) {
        if (StringUtils.isNotBlank(ldClientUser)) {
            log.debug("LaunchDarklyConfig : flagEnabled :: Flag :{} ,namespace : {} , clusterName : {}",flag,namespace,clusterName);
            LDContext ldContext =
                    LDContext.builder(ldClientUser)
                            .set(UltaCommonConstants.NAMESPACE, LDValue.of(namespace))
                            .set(UltaCommonConstants.CLUSTER, LDValue.of(clusterName))
                            .build();
            boolean rolloutEnabled =
                ldClient.boolVariation(flag, ldContext, false);
            log.debug("LaunchDarklyConfig : flagEnabled :: Rollout {} is {}", flag, rolloutEnabled);
            return rolloutEnabled;
        } else {
            log.info("LD Client User not configured, defaulting to false");
            return false;
        }
    }

    /**
   * Make a call to launch darkly to see if this rollout is on or off based on context param
   * @param flag
   * @param context
   * @return
   */
  public boolean flagEnabledContextBoolean(String flag, LDContext context) {
    log.debug(
        "LaunchDarklyConfig : flagEnabledContextBoolean :: Flag :{} ,context : {} namespace : {} , clusterName : {}",
        flag,
        context,
        namespace,
        clusterName);
    boolean rolloutEnabled = ldClient.boolVariation(flag, context, false);
      log.debug("LaunchDarklyConfig : flagEnabledContextBoolean :: Rollout {} is {}", flag, rolloutEnabled);
    return rolloutEnabled;
  }

  /**
   * Make a call to launch darkly to see if this rollout is on or off based on context param
   *
   * @param flag
   * @param context
   * @return
   */
  public int flagEnabledContextInteger(String flag, LDContext context) {
    log.debug(
        "LaunchDarklyConfig : flagEnabledContextInteger :: Flag :{} ,context : {} namespace : {} , clusterName : {}",
        flag,
        context,
        namespace,
        clusterName);
    int rolloutEnabled = ldClient.intVariation(flag, context, -1);
    log.debug("LaunchDarklyConfig : flagEnabledContextInteger :: Rollout {} is {}", flag, rolloutEnabled);
    return rolloutEnabled;
  }

  /**
   * Make a call to launch darkly to see if this rollout is on or off based on context param
   *
   * @param flag
   * @param context
   * @return
   */
  public String flagEnabledContextString(String flag, LDContext context) {
    log.debug(
        "LaunchDarklyConfig : flagEnabledContextString :: Flag :{} ,context : {} namespace : {} , clusterName : {}",
        flag,
        context,
        namespace,
        clusterName);
    String rolloutEnabled = ldClient.stringVariation(flag, context, StringUtils.EMPTY);
    log.debug("LaunchDarklyConfig : flagEnabledContextString :: Rollout {} is {}", flag, rolloutEnabled);
    return rolloutEnabled;
  }

  /**
   * Make a call to launch darkly to see if this rollout is on or off based on context param
   *
   * @param flag
   * @param id
   * @param email
   * @param kind
   * @return
   */
  public boolean flagEnabledContextBoolean(String flag, String id,String email, String kind) {

    log.debug(
        "LaunchDarklyConfig : flagEnabledContextBoolean :: Flag :{} , id : {}, email : {} , kind : {}, namespace : {} , clusterName : {}",
        flag,
        id,
        email,
        kind,
        namespace,
        clusterName);
      if(enableHarnessFME && StringUtils.isNotBlank(harnessClientUser)){
          try{
              return fetchFlagValueFromHarness(flag, id, email, kind);
          }catch(Exception e){
              log.error("Exception occurred while fetching flag value from Harness.  Exception: {}",e);
              return fetchFlagValueFromLaunchDarkly(flag, id, email, kind);
          }
    } else {
          return fetchFlagValueFromLaunchDarkly(flag, id, email, kind);
      }
  }

    /**
     * This method is usd to fetch the flag vlaue from Harness.
     * @param flag
     * @param id
     * @param email
     * @param kind
     * @return
     */
    private boolean fetchFlagValueFromHarness(String flag, String id, String email, String kind) {
        String matchingKey = StringUtils.isEmpty(id) ? UUID.randomUUID().toString() : id;
        // You can use Key if you need a bucketing key later; for now matchingKey is enough.
        Key key = new Key(matchingKey, kind);
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(UltaCommonConstants.NAMESPACE, namespace);
        attributes.put(UltaCommonConstants.CLUSTER, clusterName);
        attributes.put(UltaCommonConstants.EMAIL, email);
        String treatment = splitClient.getTreatment(key.matchingKey(), flag, attributes);
        if (StringUtils.isNotBlank(treatment) && treatment.equals(UltaCommonConstants.ON)) {
            log.debug("LaunchDarklyConfig : flagEnabledContextBoolean : fetchFlagValueFromHarness using context : flag enabled:: Rollout {} is {}", flag, treatment);
            return true;
        } else if (StringUtils.isNotBlank(treatment) && treatment.equals(UltaCommonConstants.OFF)) {
            log.debug("LaunchDarklyConfig : flagEnabledContextBoolean : fetchFlagValueFromHarness using context : flag enabled :: Rollout {} is {}", flag, treatment);
            return false;
        } else if (StringUtils.isNotBlank(treatment) && treatment.equals(UltaCommonConstants.CONTROL)) {
            //If the flag is not configured in Harnss then will fall back on LD.
            log.debug("LaunchDarklyConfig : flagEnabledContextBoolean : fetchFlagValueFromHarness : Received treatment as control ::  Rollout {} is {}", flag, treatment);
            return fetchFlagValueFromLaunchDarkly(flag, id, email, kind);
        } else {
            return false;
        }
    }

    /**
     *  This method is used to fetch the flag value from launchdarkly.
     * @param flag
     * @param id
     * @param email
     * @param kind
     * @return
     */
    private boolean fetchFlagValueFromLaunchDarkly(String flag, String id, String email, String kind) {
        ContextBuilder builder = LDContext.builder(kind);
        if (!StringUtils.isEmpty(id)) {
            builder.key(id);
        }
        LDContext ldContext =
                builder
                        .set(UltaCommonConstants.EMAIL, email)
                        .set(UltaCommonConstants.NAMESPACE, LDValue.of(namespace))
                        .set(UltaCommonConstants.CLUSTER, LDValue.of(clusterName))
                        .build();
        boolean rolloutEnabled = ldClient.boolVariation(flag, ldContext, false);
        log.debug("LaunchDarklyConfig : flagEnabledContextBoolean :: Rollout {} is {}", flag, rolloutEnabled);
        return rolloutEnabled;
    }

    /**
   * Make a call to launch darkly to see if this rollout is on or off based on context param
   *
   * @param flag
   * @param id
   * @param email
   * @param kind
   * @return
   */
  public int flagEnabledContextInteger(String flag, String id, String email, String kind) {

    log.debug(
        "LaunchDarklyConfig : flagEnabledContextInteger :: Flag :{} , id : {}, email : {} , kind : {}, namespace : {} , clusterName : {}",
        flag,
        id,
        email,
        kind,
        namespace,
        clusterName);

    ContextBuilder builder = LDContext.builder(kind);
      if (!StringUtils.isEmpty(id)) {
          builder.key(id);
      }
    LDContext ldContext =
        builder
            .set(UltaCommonConstants.EMAIL, email)
            .set(UltaCommonConstants.NAMESPACE, LDValue.of(namespace))
            .set(UltaCommonConstants.CLUSTER, LDValue.of(clusterName))
            .build();
    int rolloutEnabled = ldClient.intVariation(flag, ldContext, -1);
     log.debug("LaunchDarklyConfig : flagEnabledContextInteger :: Rollout {} is {}", flag, rolloutEnabled);
    return rolloutEnabled;
  }

  /**
   * Make a call to launch darkly to see if this rollout is on or off based on context param
   *
   * @param flag
   * @param id
   * @param email
   * @param kind
   * @return
   */
  public String flagEnabledContextString(String flag, String id, String email, String kind) {

    log.debug(
        "LaunchDarklyConfig : flagEnabledContextString :: Flag :{} , id : {}, email : {} , kind : {}, namespace : {} , clusterName : {}",
        flag,
        id,
        email,
        kind,
        namespace,
        clusterName);
    ContextBuilder builder = LDContext.builder(kind);
      if (!StringUtils.isEmpty(id)) {
          builder.key(id);
      }
    LDContext ldContext =
        builder
            .set(UltaCommonConstants.EMAIL, email)
            .set(UltaCommonConstants.NAMESPACE, LDValue.of(namespace))
            .set(UltaCommonConstants.CLUSTER, LDValue.of(clusterName))
            .build();
    String rolloutEnabled = ldClient.stringVariation(flag, ldContext, StringUtils.EMPTY);
      log.debug("LaunchDarklyConfig : flagEnabledContextString :: Rollout {} is {}", flag, rolloutEnabled);
    return rolloutEnabled;
  }
    /**
     * Make a call to launch darkly to see if this rollout is on or off
     * @param flag
     * @return
     */
  public int flagIntValue(String flag) {
      if(enableHarnessFME && StringUtils.isNotBlank(harnessClientUser)){
          try{
              return fetchIntFlagValueFromHarness(flag);
          }catch(Exception e){
              log.error("Exception occurred while fetching flag value from Harness.  Exception: {}",e);
              return fetchFlagIntValueFromLaunchDarkly(flag);
          }
    } else {
      return fetchFlagIntValueFromLaunchDarkly(flag);
      }
  }

    /**
     * This method is used to fetch the int flag value from harness.
     * @param flag
     * @return
     */
    private int fetchIntFlagValueFromHarness(String flag) {
        log.debug(
                "Harness : fetchIntFlagValueFromHarness :: Flag :{} ,namespace : {} , clusterName : {}",
                flag,
                namespace,
                clusterName);
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(UltaCommonConstants.NAMESPACE, namespace);
        attributes.put(UltaCommonConstants.CLUSTER, clusterName);
        String treatment = splitClient.getTreatment(harnessClientUser, flag, attributes);
        //Check that the treatment contains only numbers.
        if (StringUtils.isNotBlank(treatment) && treatment.matches("\\d+")) {
            log.debug("LaunchDarklyConfig : flagIntValue : fetchFlagValueFromHarness : Flag Enabled :: Rollout {} is {}", flag, treatment);
            return Integer.valueOf(treatment);
        }  else if (StringUtils.isNotBlank(treatment) && treatment.equals(UltaCommonConstants.CONTROL)) {
            //If the flag is not configured in Harness, it will fall back to LD.
            log.debug("LaunchDarklyConfig : flagIntValue : fetchFlagValueFromHarness : Received treatment as control ::  Rollout {} is {}", flag, treatment);
            return fetchFlagIntValueFromLaunchDarkly(flag);
        }else {
            return -1;
        }
    }

    /**
     *  This method is used to fetch the flag value from LaunchDarkly
     * @param flag
     * @return
     */
    private int fetchFlagIntValueFromLaunchDarkly(String flag) {
        if (StringUtils.isNotBlank(ldClientUser)) {
      LDContext ldContext =
          LDContext.builder(ldClientUser)
              .set(UltaCommonConstants.NAMESPACE, LDValue.of(namespace))
              .set(UltaCommonConstants.CLUSTER, LDValue.of(clusterName))
              .build();
      int flagConfigValue = ldClient.intVariation(flag, ldContext, -1);
        log.debug("LaunchDarklyConfig : fetchFlagIntValueFromLaunchDarkly :: Rollout {} is {}", flag, flagConfigValue);
            return flagConfigValue;
        } else {
            log.info("LD Client User not configured, defaulting to -1");
            return -1;
        }
    }

    /**
     * Make a call to launch darkly to see if this rollout is on or off
     * @param flag
     * @return
     */
    public String flagStringValue(String flag) {
      if (StringUtils.isNotBlank(ldClientUser)) {
          log.debug(
          "LaunchDarklyConfig : flagStringValue :: flag : {} ,namespace : {} , clusterName : {}",
          flag,
          namespace,
          clusterName);
        LDContext ldContext =
                LDContext.builder(ldClientUser)
                        .set(UltaCommonConstants.NAMESPACE, LDValue.of(namespace))
                        .set(UltaCommonConstants.CLUSTER, LDValue.of(clusterName))
                        .build();

      String flagConfigValue = ldClient.stringVariation(flag, ldContext, StringUtils.EMPTY);
      log.debug(
          "LaunchDarklyConfig : flagStringValue :: flag {} Config Value is {}",
          flag,
          flagConfigValue);
      return flagConfigValue;
      } else {
          log.info(
                  "LaunchDarklyConfig : flagStringValue :: LD Client User not configured, defaulting to empty string");
          return StringUtils.EMPTY;
      }
    }
}