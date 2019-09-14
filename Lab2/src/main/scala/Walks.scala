trait Walks {
  // What does this line mean?
    this: Animal =>
  //
  //  // Is this abstract or concrete (implemented) member?
  //  // Why `name` parameter is available here?
    def walk: String = s"$name is walking"
}
