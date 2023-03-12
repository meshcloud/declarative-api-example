package io.meshcloud.example.declarativeapi.model

interface IApiObject {
  val apiVersion: String
  val kind: ObjectKind

  companion object {
    // Used for sorting a batch of import objects to make sure the dependencies are in order
    // (Users created before Groups, etc).
    // Note: This is in reverse order, the first kind will be imported last.
    val ORDER: Comparator<IApiObject> = compareBy(
      { it.kind == ObjectKind.Group },
      { it.kind == ObjectKind.User }
    )
  }

  /**
   * This contains a human readable hint so you can easily identify
   * this object e.g. in the YAML file it was coming from.
   */
  val meaningfulIdentifier: String
}
