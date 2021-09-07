package admin

import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.Int
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public data class GetAdminCredentialsParams(
  public val username: String?
)

public class GetAdminCredentialsParamSetter : ParamSetter<GetAdminCredentialsParams> {
  public override fun map(ps: PreparedStatement, params: GetAdminCredentialsParams): Unit {
    ps.setObject(1, params.username)
  }
}

public data class GetAdminCredentialsResult(
  public val id: Int,
  public val username: String,
  public val password: String
)

public class GetAdminCredentialsRowMapper : RowMapper<GetAdminCredentialsResult> {
  public override fun map(rs: ResultSet): GetAdminCredentialsResult = GetAdminCredentialsResult(
  id = rs.getObject("id") as kotlin.Int,
    username = rs.getObject("username") as kotlin.String,
    password = rs.getObject("password") as kotlin.String)
}

public class GetAdminCredentialsQuery : Query<GetAdminCredentialsParams, GetAdminCredentialsResult>
    {
  public override val sql: String = """
      |SELECT * FROM admin
      |WHERE username = ?;
      |""".trimMargin()

  public override val mapper: RowMapper<GetAdminCredentialsResult> = GetAdminCredentialsRowMapper()

  public override val paramSetter: ParamSetter<GetAdminCredentialsParams> =
      GetAdminCredentialsParamSetter()
}
