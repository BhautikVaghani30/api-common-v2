/*
 * Copyright (C) 2024 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UltaAuth0CiamRequest {
  private String profileId;
  private String emailId;
  private String auth0Id;
  private String status;
  private boolean updateLastActivity;
  private String emailVerifiedAt;
  private String phoneVerifiedAt;
  private String phone;
  private String mfaEnrolled;
  private String tempMfaPhoneVerifiedFlag;
  private String tempMfaEmailVerifiedFlag;
}