/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.data;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.ulta.dsp.common.domain.data.loyalty.UltaLoyaltyMemberDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UltaProfile {
  private UltaField firstName;
  private UltaField lastName;
  private UltaField emailAddress;
  private UltaField password;
  private UltaField currentPassword;
  private UltaField newPassword;
  private UltaField memberId;
  private boolean emailOptIn;
  private boolean loyaltyOptIn;
  private String digitalId;
  private UltaField dateOfBirth;
  private UltaField phoneNumber;
  private UltaField createdAt;
  private UltaLoyaltyMemberDetails loyaltyMemberProfile;
}