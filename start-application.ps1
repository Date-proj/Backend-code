# PowerShell script to start Farmer Management System with optimized memory settings

Write-Host "Starting Farmer Management System with optimized memory settings..." -ForegroundColor Green

# Set JVM memory options to prevent OOM errors
$JAVA_OPTS = "-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdump.hprof"

# Set additional JVM options for better performance
$JAVA_OPTS += " -XX:+UseStringDeduplication -XX:+UseCompressedOops -XX:+UseCompressedClassPointers"

# Set garbage collection options
$JAVA_OPTS += " -XX:G1HeapRegionSize=16m -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40"

# Set memory management options
$JAVA_OPTS += " -XX:+DisableExplicitGC"

Write-Host "JVM Options: $JAVA_OPTS" -ForegroundColor Yellow
Write-Host ""

# Check if Maven is available
try {
    $mvnVersion = mvn -version 2>$null
    if ($LASTEXITCODE -ne 0) {
        throw "Maven not found"
    }
    Write-Host "Maven found: $($mvnVersion | Select-Object -First 1)" -ForegroundColor Green
} catch {
    Write-Host "Error: Maven is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Maven and try again" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Clean and compile the project
Write-Host "Cleaning and compiling the project..." -ForegroundColor Yellow
mvn clean compile -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error: Compilation failed" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Compilation successful!" -ForegroundColor Green

# Start the application with optimized settings
Write-Host "Starting Spring Boot application..." -ForegroundColor Yellow
Write-Host "Application will be available at: http://ec2-13-48-25-176.eu-north-1.compute.amazonaws.com:8080" -ForegroundColor Cyan

mvn spring-boot:run -Dspring-boot.run.jvmArguments="$JAVA_OPTS"

Read-Host "Press Enter to exit" 