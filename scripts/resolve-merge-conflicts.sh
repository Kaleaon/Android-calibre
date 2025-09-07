#!/bin/bash

# GitHub Merge Conflict Resolution Script
# This script uses the GitHub API to help resolve merge conflicts automatically

# GitHub Personal Access Token
GITHUB_TOKEN="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"

# Get repository information
REPO_OWNER="$(echo "$GITHUB_REPOSITORY" | cut -d'/' -f1)"
REPO_NAME="$(echo "$GITHUB_REPOSITORY" | cut -d'/' -f2)"
PR_NUMBER="$GITHUB_EVENT_NUMBER"

echo "Attempting to resolve merge conflicts for PR #$PR_NUMBER in $GITHUB_REPOSITORY"

# Function to call GitHub API
call_github_api() {
    local endpoint="$1"
    local method="${2:-GET}"
    local data="$3"
    
    curl -s -X "$method" \
        -H "Authorization: token $GITHUB_TOKEN" \
        -H "Accept: application/vnd.github.v3+json" \
        -H "Content-Type: application/json" \
        ${data:+-d "$data"} \
        "https://api.github.com$endpoint"
}

# Get the list of conflicted files
conflicted_files=$(git diff --name-only --diff-filter=U)

if [ -z "$conflicted_files" ]; then
    echo "No merge conflicts detected."
    exit 0
fi

echo "Conflicted files detected:"
echo "$conflicted_files"

# Simple conflict resolution strategies
for file in $conflicted_files; do
    echo "Resolving conflicts in: $file"
    
    # For this basic implementation, we'll use a simple strategy:
    # - Accept incoming changes for documentation files
    # - Accept current changes for code files
    # - Manual resolution for build files
    
    if [[ "$file" == *.md || "$file" == *.txt || "$file" == *.rst ]]; then
        echo "  Using incoming changes for documentation file: $file"
        git checkout --theirs "$file"
        git add "$file"
    elif [[ "$file" == *.gradle* || "$file" == *.properties || "$file" == *.yml || "$file" == *.yaml ]]; then
        echo "  Manual resolution required for build/config file: $file"
        # For build files, we'll add conflict markers and let humans resolve
        echo "  Keeping conflict markers for manual resolution"
        git add "$file"
    else
        echo "  Using current changes for code file: $file"
        git checkout --ours "$file"
        git add "$file"
    fi
done

echo "Merge conflict resolution completed."

# Verify that all conflicts are resolved
remaining_conflicts=$(git diff --name-only --diff-filter=U)
if [ -n "$remaining_conflicts" ]; then
    echo "Warning: Some conflicts still need manual resolution:"
    echo "$remaining_conflicts"
    exit 1
fi

echo "All conflicts resolved successfully."
exit 0