/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.paymentinterfaces.rts.adapter;

import com.ulta.dsp.common.domain.data.UltaCreditCard;
import com.ulta.dsp.common.domain.data.UltaCreditCardAuthInfo;
import com.ulta.dsp.common.model.UltaMessage;
import com.ulta.dsp.common.webclient.UltaWebClientBuilderService;
import com.ulta.dsp.paymentinterfaces.constants.UltaErrorMessages;
import com.ulta.dsp.paymentinterfaces.constants.UltaPaymentConstants;
import com.ulta.dsp.paymentinterfaces.creditcard.adapter.UltaCreditCardAdapter;
import com.ulta.dsp.paymentinterfaces.creditcard.model.common.UltaRTSRequest;
import com.ulta.dsp.paymentinterfaces.creditcard.model.response.UltaCSResponse;
import com.ulta.dsp.paymentinterfaces.creditcard.model.response.UltaRTSResponse;
import com.ulta.dsp.paymentinterfaces.i18n.UltaResourceManager;
import com.ulta.dsp.paymentinterfaces.model.common.UltaAuditData;
import com.ulta.dsp.paymentinterfaces.model.common.UltaCartType;
import com.ulta.dsp.paymentinterfaces.model.common.UltaCreditCardAuthData;
import com.ulta.dsp.paymentinterfaces.model.common.UltaOrderData;
import com.ulta.dsp.paymentinterfaces.model.common.UltaPaymentMethod;
import com.ulta.dsp.paymentinterfaces.model.request.UltaAuthorizePaymentRequest;
import com.ulta.dsp.paymentinterfaces.model.request.UltaPaymentMetaDataRequest;
import com.ulta.dsp.paymentinterfaces.model.request.UltaVoidPaymentRequest;
import com.ulta.dsp.paymentinterfaces.model.response.UltaCreditCardMetaData;
import com.ulta.dsp.paymentinterfaces.model.response.UltaPaymentAuthData;
import com.ulta.dsp.paymentinterfaces.model.response.UltaVoidPaymentInfo;
import com.ulta.dsp.paymentinterfaces.properties.UltaRTSProperties;
import com.ulta.dsp.paymentinterfaces.rts.helper.UltaRTSHelper;
import com.ulta.dsp.paymentinterfaces.rts.helper.UltaRTSValidationHelper;
import com.ulta.dsp.paymentinterfaces.rts.mapper.UltaRTSResponseMapper;
import com.ulta.dsp.paymentinterfaces.service.UltaAuditService;
import com.ulta.dsp.paymentinterfaces.utils.UltaJsonParserUtils;
import com.ulta.dsp.paymentinterfaces.utils.UltaPaymentUtils;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.xml.bind.JAXB;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import reactor.core.publisher.Mono;

@Slf4j
public class UltaRTSAdapterImpl implements UltaCreditCardAdapter {
  @Value(("${payment.gateway.request.response.truncation.length}"))
  private int truncationLength;
  private UltaRTSProperties ultaRTSProperties;

  @Autowired private UltaWebClientBuilderService ultaWebClientBuilderService;

  @Autowired
  private UltaAuditService ultaAuditService;

  public UltaRTSAdapterImpl(@Valid UltaRTSProperties ultaRTSProperties) {
    this.ultaRTSProperties = ultaRTSProperties;
  }
  @PostConstruct
  public void init() {
    rtsWebClient =
        ultaWebClientBuilderService
            .createExternalWebClient(UltaPaymentConstants.WEBCLIENT_NAME_EXTERNAL_RTS)
            .baseUrl(ultaRTSProperties.getBaseUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, ultaRTSProperties.getContentType())
            .build();
  }

  /**
   * This method is used to interact with RTS for generating the encryption keys
   *
   * @param metaDataRequest
   * @return
   */
  @Override
  public Mono<UltaCSResponse<UltaCreditCardMetaData>> generateKeys(
      UltaPaymentMetaDataRequest metaDataRequest) {
    return Mono.empty();
  }

  /**
   * This method is used to interact with RTS for authorizing the payment.
   *
   * @param request
   * @param paymentId
   * @param ultaAuditData
   * @return UltaCreditCardAuthData
   */
  @Override
  public Mono<UltaCreditCardAuthData> authorizePayment(
      UltaAuthorizePaymentRequest request,
      UltaCreditCard creditCardPayment,
      String paymentId,
      UltaPaymentAuthData paymentAuthData,
      UltaAuditData ultaAuditData) {
    if (log.isDebugEnabled()) {
      log.debug(
          "authorizePayment - request : {} - creditCardPayment : {} - paymentId : {}, paymentAuthData : {}",
          UltaJsonParserUtils.getJsonFromObject(request),
          UltaJsonParserUtils.getJsonFromObject(creditCardPayment),
          paymentId,
          UltaJsonParserUtils.getJsonFromObject(paymentAuthData));
    }
    Mono<UltaCreditCardAuthData> creditCardAuthData;
    String errorMessage =
        UltaRTSValidationHelper.validateAuthorizationRequest(request, creditCardPayment);
    ultaAuditData.setPaymentMethod(UltaPaymentMethod.CREDITCARD.getValue());
    if (StringUtils.isNotEmpty(errorMessage)) {
      if (log.isDebugEnabled()) {
        log.debug("authorizePayment - errorMessage : {} ", errorMessage);
      }
      creditCardAuthData =
          Mono.just(
                  UltaPaymentUtils.createErrorResponseAuthData(
                      errorMessage, paymentId, paymentAuthData))
              .map(
                  ultaCreditCardAuthData -> {
                    UltaPaymentUtils.setCreatedDateAndUpdateAuditData(
                        ultaAuditData,
                        UltaPaymentConstants.FAIL,
                        null,
                        ultaCreditCardAuthData.getMessage().getCode());
                    return ultaCreditCardAuthData;
                  });
    } else {
      UltaOrderData orderDraft = request.getOrderDraft();
      UltaCartType cartType = UltaPaymentUtils.getCartType(orderDraft);
      UltaRTSRequest rtsRequest =
          UltaRTSHelper.populateAuthorizeRTSRequest(
              orderDraft, ultaRTSProperties, creditCardPayment, cartType);

      if (log.isDebugEnabled()) {
        log.debug(
            "authorizePayment - rtsRequest : {}, appliedMid : {} ",
            UltaJsonParserUtils.getJsonFromObject(rtsRequest),
            cartType.getAppliedMid());
      }
      creditCardAuthData =
          this.getRTSWebClient(HttpMethod.POST, null)
              .body(Mono.just(rtsRequest), UltaRTSRequest.class)
              .exchangeToMono(result -> result.bodyToMono(String.class))
              .map(UltaRTSAdapterImpl::constructRTSResponse)
              .map(
                  authResponse ->
                      processAuthorizeDataResponse(
                          authResponse, rtsRequest, paymentId, ultaAuditData, cartType))
              .doOnSuccess(
                  result -> {
                    if (log.isDebugEnabled()) {
                      log.debug(
                          "authorizePayment RTS - result : {}, appliedMid : {} - success",
                          UltaJsonParserUtils.getJsonFromObject(result),
                          cartType.getAppliedMid());
                    }
                  })
              .doOnError(
                  ex -> {
                    log.error("authorizePayment - error : ", ex);
                    UltaPaymentUtils.setCreatedDataAndUpdateAuditData(
                        ultaAuditData,
                        UltaErrorMessages.RTS_AUTHORIZE_REQUEST_FAILURE,
                        rtsRequest,
                        null,
                        truncationLength,
                        ex.getLocalizedMessage());
                  })
              .onErrorReturn(
                  UltaPaymentUtils.createErrorResponseAuthDataInfo(
                      UltaErrorMessages.ULTA_RTS_FAULT_MSG_CODE, paymentId, paymentAuthData))
              .map(
                  val -> {
                    if (val.getMessage() != null
                        && StringUtils.equals(
                            val.getMessage().getType(), UltaPaymentConstants.ERROR)) {
                      UltaPaymentUtils.setCreatedDataAndUpdateAuditData(
                          ultaAuditData,
                          val.getMessage().getCode(),
                          rtsRequest,
                          ultaAuditData.getPaymentGatewayResponse(),
                          truncationLength,
                          null);
                    }
                    return val;
                  });
    }
    return creditCardAuthData;
  }

  /**
   * This method is used to construct rts WebClient
   *
   * @param method
   * @param uri
   * @param uriVariables
   */
  private RequestBodySpec getRTSWebClient(HttpMethod method, String uri, Object... uriVariables) {

    return this.rtsWebClient
        .method(method)
        .uri(uri, uriVariables)
        .headers(
            headers ->
                headers.setContentType(
                    MediaType.parseMediaType(ultaRTSProperties.getContentType())));
  }

  /**
   * This method is used to interact with RTS for void payment.
   *
   * @param request
   * @param paymentId
   * @param ultaAuditData
   * @return UltaVoidPaymentInfo
   */
  @Override
  public Mono<UltaVoidPaymentInfo> voidPayment(
      UltaVoidPaymentRequest request,
      UltaCreditCard creditCardPayment,
      String paymentId,
      UltaAuditData ultaAuditData) {
    if (log.isDebugEnabled()) {
      log.debug(
          "voidPayment - void RTS payment - request : {} - creditCardPayment : {} - paymentId : {}",
          UltaJsonParserUtils.getJsonFromObject(request),
          UltaJsonParserUtils.getJsonFromObject(creditCardPayment),
          paymentId);
    }
    Mono<UltaVoidPaymentInfo> ultaVoidPaymentInfoResponse;
    String errorMessage =
        UltaRTSValidationHelper.validateVoidRTSRequest(request, creditCardPayment);

    if (StringUtils.isNotEmpty(errorMessage)) {
      ultaVoidPaymentInfoResponse =
          Mono.just(UltaPaymentUtils.createErrorResponseVoidData(errorMessage, paymentId));
    } else {
      UltaOrderData orderDraft = request.getOrderDraft();
      UltaCartType cartType = UltaPaymentUtils.getCartType(orderDraft);
      UltaRTSRequest rtsRequest =
          UltaRTSHelper.populateVoidRequest(
              orderDraft, ultaRTSProperties, creditCardPayment, cartType);
      ultaVoidPaymentInfoResponse =
          this.getRTSWebClient(HttpMethod.POST, null)
              .body(Mono.just(rtsRequest), UltaRTSRequest.class)
              .exchangeToMono(result -> result.bodyToMono(String.class))
              .map(UltaRTSAdapterImpl::constructRTSResponse)
              .map(
                  ultaRTSResponse -> {
                    if (!UltaPaymentConstants.RTS_SUCCESS_ACTION_CODE.equals(
                        ultaRTSResponse.getActionCode())) {
                      UltaPaymentUtils.setCreatedDataAndUpdateAuditData(
                          ultaAuditData, null, rtsRequest, ultaRTSResponse, truncationLength, null);
                    }
                    return ultaRTSResponse;
                  })
              .map(result -> processVoidPaymentDataResponse(result, paymentId,creditCardPayment.getOrderId()))
              .doOnSuccess(
                  result -> {
                    log.debug("voidPayment - result : {} - success", result);
                  })
              .doOnError(
                  ex -> {
                    log.error("authorizePayment - error : {}", ex);
                    UltaPaymentUtils.setCreatedDataAndUpdateAuditData(
                        ultaAuditData,
                        UltaErrorMessages.RTS_VOID_REQUEST_FAILURE,
                        rtsRequest,
                        null,
                        truncationLength,
                        ex.getLocalizedMessage());
                  })
              .onErrorReturn(
                  UltaPaymentUtils.createErrorResponseVoidData(
                      UltaErrorMessages.ULTA_RTS_FAULT_MSG_CODE, paymentId));
    }
    return ultaVoidPaymentInfoResponse;
  }

  /**
   * Method to populate void payment response details
   *
   * @param voidResponse
   * @param paymentId
   * @return
   */
  public UltaVoidPaymentInfo processVoidPaymentDataResponse(
      UltaRTSResponse voidResponse, String paymentId, String orderId) {
    if (log.isDebugEnabled()) {
      log.debug(
          "processVoidPaymentDataResponse - voidResponse : {} - paymentId : {}",
          UltaJsonParserUtils.getJsonFromObject(voidResponse),
          paymentId);
    }
    UltaMessage creditCardVoidMessage;
    boolean success = false;
    if (UltaPaymentConstants.RTS_SUCCESS_ACTION_CODE.equals(voidResponse.getActionCode())) {
      success = true;
      creditCardVoidMessage =
          UltaMessage.builder()
              .type(UltaMessage.UltaMessageType.INFO.getValue())
              .code(
                  UltaResourceManager.getMessageKey(
                      UltaPaymentConstants.PAYMENT_METHOD_VOID_CREDIT_CARD_SUCCESS))
              .build();
    } else {
      creditCardVoidMessage =
          UltaMessage.builder()
              .type(UltaMessage.UltaMessageType.ERROR.getValue())
              .code(
                  UltaResourceManager.getMessageKey(
                      UltaErrorMessages.PAYMENT_METHOD_VOID_CREDIT_CARD_FAILED))
              .build();
    }

    return UltaVoidPaymentInfo.of(
        paymentId,
        success,
        null,
        creditCardVoidMessage,
        orderId,
        UltaPaymentMethod.CREDITCARD.getValue());
  }

  /**
   * Method to populate RTS authorization response details
   *
   * @param authResponse
   * @param rtsRequest
   * @param paymentId
   * @param ultaAuditData
   * @param cartType
   * @return UltaCreditCardAuthData
   */
  private UltaCreditCardAuthData processAuthorizeDataResponse(
      UltaRTSResponse authResponse,
      UltaRTSRequest rtsRequest,
      String paymentId,
      UltaAuditData ultaAuditData,
      UltaCartType cartType) {
    if (log.isDebugEnabled()) {
      log.debug(
          "processAuthorizeDataResponse - authResponse : {} - paymentId : {}, appliedMid : {}",
          UltaJsonParserUtils.getJsonFromObject(authResponse),
          paymentId,
          cartType.getAppliedMid());
    }
    UltaCreditCardAuthInfo authInfo =
        Mappers.getMapper(UltaRTSResponseMapper.class).mapToCreditCardResponse(authResponse);
    UltaMessage creditCardAuthMessage = null;

    if (Objects.nonNull(authInfo.getStatus())) {
      if (authInfo
          .getStatus()
          .equalsIgnoreCase(UltaPaymentConstants.CREDIT_CARD_AUTHORIZED_STATUS)) {
        creditCardAuthMessage =
            UltaMessage.builder()
                .type(UltaMessage.UltaMessageType.INFO.getValue())
                .code(
                    UltaResourceManager.getMessageKey(
                        UltaPaymentConstants.PAYMENT_METHOD_AUTH_CREDIT_CARD_SUCCESS))
                .build();
      } else if (authInfo
          .getStatus()
          .equalsIgnoreCase(UltaPaymentConstants.CREDIT_CARD_DECLINED_STATUS)) {
        creditCardAuthMessage =
            UltaMessage.builder()
                .type(UltaMessage.UltaMessageType.ERROR.getValue())
                .code(
                    UltaResourceManager.getMessageKey(
                        UltaErrorMessages.PAYMENT_METHOD_AUTH_CREDIT_CARD_DECLINED))
                .build();
      }
      if (!UltaPaymentConstants.CREDIT_CARD_AUTHORIZED_STATUS.equalsIgnoreCase(
          authInfo.getStatus())) {
        UltaPaymentUtils.setCreatedDataAndUpdateAuditData(
            ultaAuditData,
            Optional.ofNullable(creditCardAuthMessage).map(UltaMessage::getCode).orElse(null),
            rtsRequest,
            authResponse,
            truncationLength,
            null);
      }
    }
    return UltaCreditCardAuthData.builder()
        .id(paymentId)
        .authInfo(authInfo)
        .appliedMid(cartType.getAppliedMid())
        .message(creditCardAuthMessage)
        .build();
  }

  /**
   * This Method is to convert the XML string to the POJO object
   *
   * @param decodedstring
   * @return
   */
  private static UltaRTSResponse constructRTSResponse(String decodedstring) {
    if (log.isDebugEnabled()) {
      log.debug("Entity name - UltaRTSAdapterImpl - constructRTSResponse - decodedstring {} ", decodedstring);
    }
    InputStream is = new ByteArrayInputStream(decodedstring.getBytes(StandardCharsets.UTF_16));
    return JAXB.unmarshal(is, UltaRTSResponse.class);
  }

}