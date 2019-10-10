package com.qingzhenyun.common.jooq.binding

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.jooq.*
import org.jooq.impl.DSL
import java.sql.SQLException
import java.sql.SQLFeatureNotSupportedException
import java.sql.Types
import java.util.*

class CommonJsonBinding : Binding<Any, Map<String, String>> {
    override fun register(ctx: BindingRegisterContext<Map<String, String>>) {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR)
    }

    override fun sql(ctx: BindingSQLContext<Map<String, String>>) {
        // Depending on how you generate your SQL, you may need to explicitly distinguish
        // between jOOQ generating bind variables or inlined literals. If so, use this check:
        // ctx.render().paramType() == INLINED
        ctx.render().visit(DSL.`val`(ctx.convert(converter()).value())).sql("::json")
    }

    override fun converter(): Converter<Any, Map<String, String>> {
        return object : Converter<Any, Map<String, String>> {
            override fun from(t: Any?): Map<String, String>? {
                //logger.info("Convert data {}",t)
                if (t == null) {
                    return null
                }
                val typeRef = object : TypeReference<HashMap<String, String>>() {}
                return objectMapper.readValue(t.toString(), typeRef)
            }

            override fun to(u: Map<String, String>?): String? {
                return if (u == null || u.isEmpty()) null else objectMapper.writeValueAsString(u)
            }

            override fun fromType(): Class<Any> {
                return Any::class.java
            }

            override fun toType(): Class<Map<String, String>> {
                @Suppress("UNCHECKED_CAST")
                return Map::class.java as Class<Map<String, String>>
            }
        }
    }


    // Converting the JsonElement to a String value and setting that on a JDBC PreparedStatement
    @Throws(SQLException::class)
    override fun set(ctx: BindingSetStatementContext<Map<String, String>>) {
        ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), null))
    }

    // Getting a String value from a JDBC ResultSet and converting that to a JsonElement
    @Throws(SQLException::class)
    override fun get(ctx: BindingGetResultSetContext<Map<String, String>>) {
        ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()))
    }

    // Getting a String value from a JDBC CallableStatement and converting that to a JsonElement
    @Throws(SQLException::class)
    override fun get(ctx: BindingGetStatementContext<Map<String, String>>) {
        ctx.convert(converter()).value(ctx.statement().getString(ctx.index()))
    }

    // Setting a value on a JDBC SQLOutput (useful for Oracle OBJECT types)
    @Throws(SQLException::class)
    override fun set(ctx: BindingSetSQLOutputContext<Map<String, String>>) {
        throw SQLFeatureNotSupportedException()
    }

    // Getting a value from a JDBC SQLInput (useful for Oracle OBJECT types)
    @Throws(SQLException::class)
    override fun get(ctx: BindingGetSQLInputContext<Map<String, String>>) {
        throw SQLFeatureNotSupportedException()
    }

    companion object {
        val objectMapper = ObjectMapper()
        // val logger = LoggerFactory.getLogger(CommonJsonBinding::class.java)
    }
}
