# IDEA Startup Fix

## Root Cause

The project itself compiles correctly with Maven and Java 17.

The startup failure in IntelliJ IDEA is caused by wrong IDEA project settings:

- `.idea/misc.xml` was using `languageLevel="JDK_24"`
- `.idea/misc.xml` was using `project-jdk-name="zulu-1.8"`
- `.idea/workspace.xml` was using Maven importer JDK `11`

This is inconsistent with the project requirement:

- Java `17`

That mismatch can trigger compile-time internal errors such as:

- `java.lang.ExceptionInInitializerError`
- `com.sun.tools.javac.code.TypeTag :: UNKNOWN`

## Files Fixed

The following IDEA config files were corrected:

- `.idea/misc.xml`
- `.idea/workspace.xml`

## What You Still Need To Do In IDEA

### 1. Confirm Project SDK

Open:

- `File -> Project Structure -> Project`

Check:

- `Project SDK` = local JDK 17
- `Project language level` = `SDK default` or `17`

If IDEA shows red or cannot find the SDK:

1. click `Add SDK`
2. choose local JDK path
3. select:

```text
C:\Program Files\Java\jdk-17
```

### 2. Confirm Maven Importer JDK

Open:

- `File -> Settings -> Build, Execution, Deployment -> Build Tools -> Maven`

Check:

- `JDK for importer` = `17`

### 3. Confirm Gradle Is Not Involved

This project is Maven only.
Do not import or run it as a Gradle project.

### 4. Reload Maven Project

In the right-side Maven panel:

- click `Reload All Maven Projects`

### 5. Rebuild Project

Open:

- `Build -> Rebuild Project`

### 6. Run Spring Boot Main Class

Run:

- `MindCareServicePlatformApplication`

## If It Still Fails

Use this stronger reset sequence:

1. close IDEA
2. delete `.idea`
3. delete all `*.iml` files if any exist
4. reopen the project by selecting `pom.xml`
5. let IDEA re-import as Maven project
6. set Project SDK to JDK 17 again

## Important Note

If terminal Maven works but IDEA fails,
the problem is almost always IDEA local configuration rather than project code.
