package models

import java.sql.Timestamp
import org.squeryl.annotations.Column
import models.Util

trait CreatedAtField {
  @Column(name="created_at") var createdAt: Timestamp = Util.now
}

trait UpdatedAtField {
  @Column(name="updated_at") var updatedAt: Timestamp = Util.now
}
