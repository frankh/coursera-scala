name <<= submitProjectName(pname => "progfun-"+ pname)

version := "1.0.0"

scalaVersion := "2.10.1"

scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies += "org.scalatest" %% "scalatest" % "1.9.1" % "test"

libraryDependencies += "junit" % "junit" % "4.10" % "test"

// This setting defines the project to which a solution is submitted. When creating a
// handout, the 'createHandout' task will make sure that its value is correct.
submitProjectName := "forcomp"

// See documentation in ProgFunBuild.scala
projectDetailsMap := {
val currentCourseId = "progfun-002"
Map(
  "example" ->  ProjectDetails(
                  packageName = "example",
                  assignmentPartId = "fTzFogNl",
                  maxScore = 10d,
                  styleScoreRatio = 0.2,
                  courseId=currentCourseId),
  "recfun" ->     ProjectDetails(
                  packageName = "recfun",
                  assignmentPartId = "3Rarn9Ki",
                  maxScore = 10d,
                  styleScoreRatio = 0.2,
                  courseId=currentCourseId),
  "funsets" ->    ProjectDetails(
                  packageName = "funsets",
                  assignmentPartId = "fBXOL6Qd",
                  maxScore = 10d,
                  styleScoreRatio = 0.2,
                  courseId=currentCourseId),
  "objsets" ->    ProjectDetails(
                  packageName = "objsets",
                  assignmentPartId = "95dMMEz7",
                  maxScore = 10d,
                  styleScoreRatio = 0.2,
                  courseId=currentCourseId),
  "patmat" ->     ProjectDetails(
                  packageName = "patmat",
                  assignmentPartId = "3gPmpcif",
                  maxScore = 10d,
                  styleScoreRatio = 0.2,
                  courseId=currentCourseId),
  "forcomp" ->    ProjectDetails(
                  packageName = "forcomp",
                  assignmentPartId = "fG1oZGIO",
                  maxScore = 10d,
                  styleScoreRatio = 0.2,
                  courseId=currentCourseId),
  "streams" ->    ProjectDetails(
                  packageName = "streams",
                  assignmentPartId = "CWKgCFCi",
                  maxScore = 10d,
                  styleScoreRatio = 0.2,
                  courseId=currentCourseId)//,
  // "simulations" -> ProjectDetails(
  //                  packageName = "simulations",
  //                  assignmentPartId = "iYs4GARk",
  //                  maxScore = 10d,
  //                  styleScoreRatio = 0.2,
  //                  courseId="progfun2-001"),
  // "interpreter" -> ProjectDetails(
  //                  packageName = "interpreter",
  //                  assignmentPartId = "1SZhe1Ut",
  //                  maxScore = 10d,
  //                  styleScoreRatio = 0.2,
  //                  courseId="progfun2-001")
)
}

// Files that we hand out to the students
handoutFiles <<= (baseDirectory, projectDetailsMap, commonSourcePackages) map { (basedir, detailsMap, commonSrcs) =>
  (projectName: String) => {
    val details = detailsMap.getOrElse(projectName, sys.error("Unknown project name: "+ projectName))
    val commonFiles = (PathFinder.empty /: commonSrcs)((files, pkg) =>
      files +++ (basedir / "src" / "main" / "scala" / pkg ** "*.scala")
    )
    (basedir / "src" / "main" / "scala" / details.packageName ** "*.scala") +++
    commonFiles +++
    (basedir / "src" / "main" / "resources" / details.packageName ** "*") +++
    (basedir / "src" / "test" / "scala" / details.packageName ** "*.scala") +++
    (basedir / "build.sbt") +++
    (basedir / "project" / "build.properties") +++
    (basedir / "project" ** ("*.scala" || "*.sbt")) +++
    (basedir / "project" / "scalastyle_config.xml") +++
    (basedir / "lib_managed" ** "*.jar") +++
    (basedir * (".classpath" || ".project")) +++
    (basedir / ".settings" / "org.scala-ide.sdt.core.prefs")
  }
}

// This setting allows to restrict the source files that are compiled and tested
// to one specific project. It should be either the empty string, in which case all
// projects are included, or one of the project names from the projectDetailsMap.
currentProject := ""

// Packages in src/main/scala that are used in every project. Included in every
// handout, submission.
commonSourcePackages += "common"

// Packages in src/test/scala that are used for grading projects. Always included
// compiling tests, grading a project.
gradingTestPackages += "grading"
