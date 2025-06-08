
package com.svenjacobs.app.leon.core.domain.sanitizer.allquery

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomainRegex
import com.svenjacobs.app.leon.core.common.regex.RegexFactory
import com.svenjacobs.app.leon.core.domain.R
import com.svenjacobs.app.leon.core.domain.sanitizer.RegexSanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class AllQuerySanitizer : RegexSanitizer(
    regex = RegexFactory.AllParameters,
) {
    override val id = SanitizerId("allquery")

    override fun getMetadata(context: Context) = Sanitizer.Metadata(
        name = context.getString(R.string.sanitizer_allquery_name),
    )

    override fun matchesDomain(input: String) = input.matchesDomainRegex(
        domain = "(ikea|kogan)\\.com",
    )
}
