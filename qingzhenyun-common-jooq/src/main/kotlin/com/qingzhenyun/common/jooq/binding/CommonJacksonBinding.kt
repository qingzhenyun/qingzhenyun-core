package com.qingzhenyun.common.jooq.binding

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.jooq.*
import org.jooq.impl.DSL
import java.sql.SQLFeatureNotSupportedException
import java.sql.Types
import java.util.*

class CommonJacksonBinding : Binding<Any, JsonNode> {
    override fun register(ctx: BindingRegisterContext<JsonNode>) {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR)
    }

    override fun sql(ctx: BindingSQLContext<JsonNode>) {
        ctx.render().visit(DSL.`val`(ctx.convert(converter()).value())).sql("::json")
    }

    override fun converter(): Converter<Any, JsonNode> {
        return object : Converter<Any, JsonNode> {
            override fun from(t: Any?): JsonNode? {
                //logger.info("Convert data {}",t)
                /*
                if (t == null) {
                    return null
                }
                val typeRef = object : TypeReference<HashMap<String, String>>() {}
                */
                return objectMapper.readTree(t.toString())
            }

            override fun to(u: JsonNode?): String? {
                return if (u == null || u.isNull) null else objectMapper.writeValueAsString(u)
            }

            override fun fromType(): Class<Any> {
                return Any::class.java
            }

            override fun toType(): Class<JsonNode> {
                @Suppress("UNCHECKED_CAST")
                return JsonNode::class.java
            }
        }
    }

    override fun get(ctx: BindingGetResultSetContext<JsonNode>) {
        ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()))
    }

    override fun get(ctx: BindingGetStatementContext<JsonNode>) {
        ctx.convert(converter()).value(ctx.statement().getString(ctx.index()))
    }

    override fun get(ctx: BindingGetSQLInputContext<JsonNode>?) {
        throw SQLFeatureNotSupportedException()
    }

    override fun set(ctx: BindingSetStatementContext<JsonNode>) {
        ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), null))
    }

    override fun set(ctx: BindingSetSQLOutputContext<JsonNode>?) {
        throw SQLFeatureNotSupportedException()
    }

    companion object {
        val objectMapper = ObjectMapper()
        // val logger = LoggerFactory.getLogger(CommonJsonBinding::class.java)
    }
}