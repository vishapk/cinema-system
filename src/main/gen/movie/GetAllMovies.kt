package movie

import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.Int
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public class GetAllMoviesParams

public class GetAllMoviesParamSetter : ParamSetter<GetAllMoviesParams> {
  public override fun map(ps: PreparedStatement, params: GetAllMoviesParams): Unit {
  }
}

public data class GetAllMoviesResult(
  public val id: Int,
  public val title: String,
  public val durationInMinutes: Int
)

public class GetAllMoviesRowMapper : RowMapper<GetAllMoviesResult> {
  public override fun map(rs: ResultSet): GetAllMoviesResult = GetAllMoviesResult(
  id = rs.getObject("id") as kotlin.Int,
    title = rs.getObject("title") as kotlin.String,
    durationInMinutes = rs.getObject("duration_in_minutes") as kotlin.Int)
}

public class GetAllMoviesQuery : Query<GetAllMoviesParams, GetAllMoviesResult> {
  public override val sql: String = """
      |SELECT * FROM movies
      |""".trimMargin()

  public override val mapper: RowMapper<GetAllMoviesResult> = GetAllMoviesRowMapper()

  public override val paramSetter: ParamSetter<GetAllMoviesParams> = GetAllMoviesParamSetter()
}
