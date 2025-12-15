# Static Analysis Report - SpotBugs

## Tool Overview

**SpotBugs** is a static code analysis tool that examines Java bytecode to detect bugs, security vulnerabilities, and code quality issues without executing the program.

## What SpotBugs Analyzes

- **Correctness**: Null pointers, resource leaks, infinite loops
- **Security**: SQL injection, hardcoded passwords, etc.
- **Performance**: Inefficient operations
- **Bad Practices**: Ignored exceptions, poor API design, violated standards, Deprecated libraries
- **Concurrency**: Race conditions, deadlocks, synchronization issues

## How to Run

```bash
# Local
cd nestuity-service
./gradlew spotbugsMain

# Docker
./dexec nestuity-service bash
./gradlew spotbugsMain
```

## View Report

Report generated at: `nestuity-service/build/reports/spotbugs/spotbugs.html`

## Analysis Results

### Gradle Configuration Issues
1. **String to Enum Deprecation (2 warnings)**
   - `effort = 'max'` should use `Effort.MAX`
   - `reportLevel = 'medium'` should use `Confidence.MEDIUM`
   - Impact: Will fail in Gradle 10.0, works with deprecation warning for now

### Code Quality Findings
2. **Deprecated API Usage** - `UserSeeder.java`
   - File uses deprecated Spring/Java APIs
   - Actions to take: Review and update to current API versions if necessary

### Summary
SpotBugs successfully analyzed our codebase. Main findings relate to build configuration and deprecated API usage in seeder code. No critical bugs or security vulnerabilities detected.

Actions taken: will ignore for now