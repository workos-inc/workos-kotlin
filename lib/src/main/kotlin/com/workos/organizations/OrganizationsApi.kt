package com.workos.organizations

import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.WorkOS
import com.workos.common.http.PaginationParams
import com.workos.common.http.RequestConfig
import com.workos.organizations.models.Organization
import com.workos.organizations.models.OrganizationList

class OrganizationsApi(val workos: WorkOS) {
  class CreateOrganizationOptions @JvmOverloads constructor(
    val name: String? = null,

    @JsonProperty("allow_profiles_outside_organization")
    val allowProfilesOutsideOrganization: Boolean? = null,

    val domains: List<String>? = null
  ) {
    class Builder {
      var name: String? = null

      var allowProfilesOutsideOrganization: Boolean? = null

      var domains: List<String>? = null

      fun name(value: String) = apply { name = value }

      fun allowProfilesOutsideOrganization(value: Boolean) = apply { allowProfilesOutsideOrganization = value }

      fun domains(value: List<String>) = apply { domains = value }

      fun build(): CreateOrganizationOptions {
        return CreateOrganizationOptions(name, allowProfilesOutsideOrganization, domains)
      }
    }

    companion object {
      fun builder(): Builder {
        return Builder()
      }
    }
  }

  fun createOrganization(options: CreateOrganizationOptions = CreateOrganizationOptions()): Organization {
    val config = RequestConfig.builder()
      .data(options)
      .build()

    return workos.post("/organizations", Organization::class.java, config)
  }

  fun deleteOrganization(id: String) {
    workos.delete("/organizations/$id")
  }

  fun getOrganization(id: String): Organization {
    return workos.get("/organizations/$id", Organization::class.java)
  }

  class ListOrganizationsOptions @JvmOverloads constructor(
    domains: List<String>? = null,
    after: String? = null,
    before: String? = null,
    limit: Int? = null
  ) : PaginationParams(after, before, limit) {
    init {
      if (domains != null) set("domains", domains.joinToString(","))
    }

    companion object {
      fun builder(): Builder {
        return Builder()
      }
    }

    class Builder : PaginationParams.Builder<ListOrganizationsOptions>(ListOrganizationsOptions()) {
      fun domains(value: List<String>) = apply { this.params["domains"] = value.joinToString(",") }
    }
  }

  fun listOrganizations(options: ListOrganizationsOptions = ListOrganizationsOptions()): OrganizationList {
    val config = RequestConfig.builder()
      .params(options)
      .build()

    return workos.get("/organizations", OrganizationList::class.java, config)
  }
}
