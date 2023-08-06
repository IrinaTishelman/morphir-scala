package org.finos.morphir
import zio.prelude.*

private[morphir] trait ModuleNamingExports { self: naming.type =>

  /**
   * A module name is a unique identifier for a module within a package. It is represented by a pth, which is a list of
   * names.
   */
  type ModuleName = ModuleName.Type

  object ModuleName extends Subtype[Path] {

    def apply(path: Path, name: Name): ModuleName = ModuleName.fromPath(path / name)

    def apply(input: String): ModuleName =
      ModuleName.fromPath(Path.fromArray(input.split('.').map(Name.fromString)))

    def fromPath(path: Path): ModuleName      = wrap(path)
    def fromString(input: String): ModuleName = ModuleName(input)

    implicit final class ModuleNameOps(val self: ModuleName) {
      @inline def localName: Name = name

      def name: Name =
        self match {
          case ModuleName(Path(IndexedSeq())) => Name.empty
          case ModuleName(Path(segments))     => segments.last
        }

      def namespace: Namespace = Namespace.fromPath(Path(self.value.segments.dropRight(1)))

      def toPath: Path = unwrap(self)

      def value: Path = unwrap(self)
    }
  }
  type ModulePath = ModulePath.Type

  object ModulePath extends Subtype[Path] {
    val empty: ModulePath               = wrap(Path.empty)
    def apply(parts: Name*): ModulePath = wrap(Path.fromList(parts.toList))

    def fromPath(path: Path): ModulePath    = wrap(path)
    def fromString(str: String): ModulePath = wrap(Path.fromString(str))

    implicit class ModulePathOps(val modulePath: ModulePath) {
      def /(name: Name): ModuleName = ModuleName.fromPath(modulePath.value / name)

      @inline def toPath: Path = modulePath.value

      @inline def value: Path = unwrap(modulePath)
    }
  }
}
