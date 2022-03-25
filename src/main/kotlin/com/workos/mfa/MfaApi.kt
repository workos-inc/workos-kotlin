package com.workos.mfa

import com.workos.WorkOS
import com.workos.common.http.RequestConfig
import com.workos.mfa.models.Challenge
import com.workos.mfa.models.Factor
import com.workos.mfa.models.VerifyFactorResponse

class MfaApi(private val workos: WorkOS) {

  /**
   * Parameters for the [enrollFactor] method.
   */
  class EnrollFactorOptions @JvmOverloads constructor(
    type: String,
    issuer: String? = null,
    user: String? = null,
    phoneNumber: String? = null
  )

  /**
   * Enrolls a Factor.
   */
  @JvmOverloads
  fun enrollFactor(enrollFactorOptions: EnrollFactorOptions): Factor {
    val config = RequestConfig.builder()
      .data(enrollFactorOptions)
      .build()

    return workos.post("/auth/factors/enroll", Factor::class.java, config)
  }

  /**
   * Parameters for the [challengeFactor] method.
   */
  class ChallengeFactorOptions @JvmOverloads constructor(
    authenticationFactorId: String,
    smsTemplate: String? = null
  )

  /**
   * Challenges a Factor.
   */
  @JvmOverloads
  fun challengeFactor(challengeFactorOptions: ChallengeFactorOptions): Challenge {
    val config = RequestConfig.builder()
      .data(challengeFactorOptions)
      .build()

    return workos.post("/auth/factors/challenge", Challenge::class.java, config)
  }

  /**
   * Parameters for the [verifyFactor] method.
   */
  class VerifyFactorOptions @JvmOverloads constructor(
    challengeId: String,
    code: String
  )

  /**
   * Verifies a Factor.
   */
  @JvmOverloads
  fun challengeFactor(verifyFactorOptions: VerifyFactorOptions): VerifyFactorResponse {
    val config = RequestConfig.builder()
      .data(verifyFactorOptions)
      .build()

    return workos.post("/auth/factors/verify", VerifyFactorResponse::class.java, config)
  }

  /**
   * Gets a Factor by id.
   */
  fun getFactor(id: String): Factor {
    return workos.get("/auth/factors/$id", Factor::class.java)
  }

  /**
   * Deletes a Factor by id.
   */
  fun deleteFactor(id: String) {
    workos.delete("/auth/factors/$id")
  }
}
