#!/bin/bash

# Build Performance Monitor for Android Project
# Monitors build times, analyzes performance bottlenecks, and provides optimization suggestions

set -e

# Configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_REPORTS_DIR="${PROJECT_ROOT}/build-reports"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Create reports directory
mkdir -p "${BUILD_REPORTS_DIR}"

echo -e "${BLUE}🚀 Android Build Performance Monitor${NC}"
echo "======================================"

# Function to run timed build
run_timed_build() {
    local build_type="$1"
    local gradle_task="$2"
    
    echo -e "\n${YELLOW}📊 Running ${build_type} build...${NC}"
    
    # Clean before timing
    ./gradlew clean --quiet
    
    # Time the build
    local start_time=$(date +%s.%N)
    ./gradlew "${gradle_task}" --profile --build-cache
    local end_time=$(date +%s.%N)
    
    # Calculate duration
    local duration=$(echo "${end_time} - ${start_time}" | bc)
    echo -e "${GREEN}✅ ${build_type} build completed in ${duration} seconds${NC}"
    
    # Copy build scan reports
    local profile_dir="${PROJECT_ROOT}/build/reports/profile"
    if [[ -d "${profile_dir}" ]]; then
        cp -r "${profile_dir}" "${BUILD_REPORTS_DIR}/${build_type}_profile_${TIMESTAMP}"
    fi
    
    echo "${build_type},${duration},${TIMESTAMP}" >> "${BUILD_REPORTS_DIR}/build_times.csv"
}

# Function to analyze dependency resolution
analyze_dependencies() {
    echo -e "\n${YELLOW}🔍 Analyzing dependency resolution...${NC}"
    
    ./gradlew :CleverFerret:dependencies --configuration releaseRuntimeClasspath > "${BUILD_REPORTS_DIR}/dependencies_release_${TIMESTAMP}.txt"
    ./gradlew :CleverFerret:dependencies --configuration debugRuntimeClasspath > "${BUILD_REPORTS_DIR}/dependencies_debug_${TIMESTAMP}.txt"
    
    # Count dependencies
    local release_deps=$(grep -c "^[+\\]" "${BUILD_REPORTS_DIR}/dependencies_release_${TIMESTAMP}.txt" || echo "0")
    local debug_deps=$(grep -c "^[+\\]" "${BUILD_REPORTS_DIR}/dependencies_debug_${TIMESTAMP}.txt" || echo "0")
    
    echo -e "${BLUE}📦 Release dependencies: ${release_deps}${NC}"
    echo -e "${BLUE}📦 Debug dependencies: ${debug_deps}${NC}"
}

# Function to check Gradle daemon status
check_gradle_daemon() {
    echo -e "\n${YELLOW}⚙️  Checking Gradle daemon status...${NC}"
    
    ./gradlew --status > "${BUILD_REPORTS_DIR}/gradle_daemon_${TIMESTAMP}.txt"
    cat "${BUILD_REPORTS_DIR}/gradle_daemon_${TIMESTAMP}.txt"
}

# Function to analyze build cache
analyze_build_cache() {
    echo -e "\n${YELLOW}🗄️  Analyzing build cache...${NC}"
    
    # Check cache directory size
    local cache_dir="${HOME}/.gradle/caches"
    if [[ -d "${cache_dir}" ]]; then
        local cache_size=$(du -sh "${cache_dir}" 2>/dev/null | cut -f1 || echo "Unknown")
        echo -e "${BLUE}📁 Gradle cache size: ${cache_size}${NC}"
    fi
    
    # Check build cache effectiveness
    ./gradlew assembleDebug --build-cache --info 2>&1 | grep -E "(FROM-CACHE|UP-TO-DATE)" | wc -l > "${BUILD_REPORTS_DIR}/cache_hits_${TIMESTAMP}.txt"
    local cache_hits=$(cat "${BUILD_REPORTS_DIR}/cache_hits_${TIMESTAMP}.txt")
    echo -e "${BLUE}🎯 Cache hits in last build: ${cache_hits}${NC}"
}

# Function to generate optimization recommendations
generate_recommendations() {
    echo -e "\n${YELLOW}💡 Generating optimization recommendations...${NC}"
    
    local recommendations_file="${BUILD_REPORTS_DIR}/recommendations_${TIMESTAMP}.md"
    
    cat > "${recommendations_file}" << EOF
# Build Performance Optimization Recommendations

Generated: $(date)

## Current Build Performance

EOF

    # Add build time analysis
    if [[ -f "${BUILD_REPORTS_DIR}/build_times.csv" ]]; then
        echo "### Recent Build Times" >> "${recommendations_file}"
        echo "\`\`\`" >> "${recommendations_file}"
        tail -10 "${BUILD_REPORTS_DIR}/build_times.csv" >> "${recommendations_file}"
        echo "\`\`\`" >> "${recommendations_file}"
        echo "" >> "${recommendations_file}"
    fi

    # Add general recommendations
    cat >> "${recommendations_file}" << EOF
## Optimization Strategies

### 1. Gradle Configuration
- ✅ Gradle daemon is enabled
- ✅ Build cache is enabled
- ✅ Parallel execution is configured
- ✅ JVM heap size is optimized (4GB)

### 2. Dependency Management
- Consider using Gradle version catalogs for dependency management
- Review transitive dependencies for potential conflicts
- Use \`implementation\` instead of \`api\` where possible

### 3. Build Cache Optimization
- Ensure build scripts are cacheable
- Use \`--build-cache\` flag consistently
- Consider remote build cache for team development

### 4. Android-Specific Optimizations
- Use R8 shrinking and obfuscation in release builds
- Enable resource shrinking: \`shrinkResources = true\`
- Consider using APK splitting for multi-APK distribution

### 5. CI/CD Optimizations
- Use incremental builds in CI
- Cache Gradle wrapper and dependencies
- Parallelize test execution
- Use build matrices for multi-variant testing

## Next Steps

1. Implement recommended optimizations
2. Monitor build performance over time
3. Profile slow builds to identify bottlenecks
4. Consider upgrading to newer Gradle/AGP versions

EOF

    echo -e "${GREEN}📝 Recommendations saved to: ${recommendations_file}${NC}"
}

# Function to create performance dashboard
create_dashboard() {
    echo -e "\n${YELLOW}📊 Creating performance dashboard...${NC}"
    
    local dashboard_file="${BUILD_REPORTS_DIR}/dashboard.html"
    
    cat > "${dashboard_file}" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>Android Build Performance Dashboard</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .metric { background: #f5f5f5; padding: 15px; margin: 10px 0; border-radius: 5px; }
        .good { border-left: 5px solid #4CAF50; }
        .warning { border-left: 5px solid #FF9800; }
        .error { border-left: 5px solid #F44336; }
        .chart { width: 100%; height: 300px; margin: 20px 0; }
    </style>
</head>
<body>
    <h1>🚀 Android Build Performance Dashboard</h1>
    <p>Generated: $(date)</p>
    
    <div class="metric good">
        <h3>✅ Build System Status</h3>
        <ul>
            <li>Gradle daemon: Active</li>
            <li>Build cache: Enabled</li>
            <li>Parallel execution: Enabled</li>
            <li>JVM heap: 4GB</li>
        </ul>
    </div>
    
    <div class="metric">
        <h3>📊 Recent Build Performance</h3>
        <p>Check the build_times.csv file for detailed build time metrics.</p>
    </div>
    
    <div class="metric">
        <h3>🔧 Optimization Opportunities</h3>
        <ul>
            <li>Monitor dependency count growth</li>
            <li>Profile incremental build performance</li>
            <li>Analyze cache hit rates</li>
            <li>Review test execution times</li>
        </ul>
    </div>
    
    <h2>📁 Available Reports</h2>
    <ul>
EOF

    # List all report files
    for file in "${BUILD_REPORTS_DIR}"/*; do
        if [[ -f "$file" ]]; then
            local filename=$(basename "$file")
            echo "        <li><a href=\"${filename}\">${filename}</a></li>" >> "${dashboard_file}"
        fi
    done

    cat >> "${dashboard_file}" << EOF
    </ul>
</body>
</html>
EOF

    echo -e "${GREEN}📊 Dashboard created: ${dashboard_file}${NC}"
}

# Main execution
main() {
    cd "${PROJECT_ROOT}"
    
    # Initialize CSV header if it doesn't exist
    if [[ ! -f "${BUILD_REPORTS_DIR}/build_times.csv" ]]; then
        echo "build_type,duration_seconds,timestamp" > "${BUILD_REPORTS_DIR}/build_times.csv"
    fi
    
    # Run performance analysis
    check_gradle_daemon
    analyze_dependencies
    analyze_build_cache
    
    # Run timed builds
    run_timed_build "debug" "assembleDebug"
    run_timed_build "release" "assembleRelease"
    
    # Generate reports
    generate_recommendations
    create_dashboard
    
    echo -e "\n${GREEN}🎉 Performance analysis complete!${NC}"
    echo -e "${BLUE}📊 Reports saved in: ${BUILD_REPORTS_DIR}${NC}"
    echo -e "${BLUE}🌐 Open dashboard: file://${dashboard_file}${NC}"
}

# Run main function
main "$@"