package show

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import kotlin.Int
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public data class SetShowPriceParams(
  public val price: Int?,
  public val id: Int?
)

public class SetShowPriceParamSetter : ParamSetter<SetShowPriceParams> {
  public override fun map(ps: PreparedStatement, params: SetShowPriceParams): Unit {
    ps.setObject(1, params.price)
    ps.setObject(2, params.id)
  }
}

public data class SetShowPriceResult(
  public val id: Int,
  public val startTime: Timestamp,
  public val endTime: Timestamp,
  public val movieId: Int,
  public val capacity: Int,
  public val availableTickets: Int,
  public val price: Int?
)

public class SetShowPriceRowMapper : RowMapper<SetShowPriceResult> {
  public override fun map(rs: ResultSet): SetShowPriceResult = SetShowPriceResult(
  id = rs.getObject("id") as kotlin.Int,
    startTime = rs.getObject("start_time") as java.sql.Timestamp,
    endTime = rs.getObject("end_time") as java.sql.Timestamp,
    movieId = rs.getObject("movie_id") as kotlin.Int,
    capacity = rs.getObject("capacity") as kotlin.Int,
    availableTickets = rs.getObject("available_tickets") as kotlin.Int,
    price = rs.getObject("price") as kotlin.Int?)
}

public class SetShowPriceQuery : Query<SetShowPriceParams, SetShowPriceResult> {
  public override val sql: String = """
      |UPDATE shows
      |    SET price = ? where id = ?
      |    returning *;
      |
      |
      |
      |
      |
      |
      |""".trimMargin()

  public override val mapper: RowMapper<SetShowPriceResult> = SetShowPriceRowMapper()

  public override val paramSetter: ParamSetter<SetShowPriceParams> = SetShowPriceParamSetter()
}
