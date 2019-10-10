/*

class PostgresJSONGsonBinding : Binding<Any, JsonNode> {
    override fun register(ctx: BindingRegisterContext<JsonNode>) {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
    }

    override fun sql(ctx: BindingSQLContext<JsonNode>) {
        // Depending on how you generate your SQL, you may need to explicitly distinguish
        // between jOOQ generating bind variables or inlined literals. If so, use this check:
        // ctx.render().paramType() == INLINED
        ctx.render().visit(DSL.`val`(ctx.convert(converter()).value())).sql("::json")
    }

    override fun converter(): Converter<Any, JsonNode> {
        return object : Converter<Any, JsonNode> {
            override fun from(t: Any?): JsonNode? {
                if(t == null){
                    return null
                }
                return objectMapper.readValue(t.toString(),JsonNode::class.java)
                //return if (t == null) JsonNull.INSTANCE else Gson().fromJson("" + t, JsonElement::class.java)
            }

            override fun to(u: JsonNode?): String? {
                return if (u == null || u.isNull) null else objectMapper.writeValueAsString(u)
            }

            override fun fromType(): Class<Any> {
                return Any::class.java
            }

            override fun toType(): Class<JsonNode> {
                return JsonNode::class.java
            }
        }
    }


    // Converting the JsonElement to a String value and setting that on a JDBC PreparedStatement
    @Throws(SQLException::class)
    override fun set(ctx: BindingSetStatementContext<JsonNode>) {
        ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), null))
    }

    // Getting a String value from a JDBC ResultSet and converting that to a JsonElement
    @Throws(SQLException::class)
    override fun get(ctx: BindingGetResultSetContext<JsonNode>) {
        ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()))
    }

    // Getting a String value from a JDBC CallableStatement and converting that to a JsonElement
    @Throws(SQLException::class)
    override fun get(ctx: BindingGetStatementContext<JsonNode>) {
        ctx.convert(converter()).value(ctx.statement().getString(ctx.index()))
    }

    // Setting a value on a JDBC SQLOutput (useful for Oracle OBJECT types)
    @Throws(SQLException::class)
    override fun set(ctx: BindingSetSQLOutputContext<JsonNode>) {
        throw SQLFeatureNotSupportedException()
    }

    // Getting a value from a JDBC SQLInput (useful for Oracle OBJECT types)
    @Throws(SQLException::class)
    override fun get(ctx: BindingGetSQLInputContext<JsonNode>) {
        throw SQLFeatureNotSupportedException()
    }

    companion object {
        val objectMapper = ObjectMapper()
    }
}

  */
