#!/usr/bin/env bash
set -e

# Check to make sure that an argument was passed in
if [-z "$0"]; then
    echo "[error] usage: hotswap.sh <new redis url>"
    exit 1
fi

# Get the current redis url:
current=$(sed -En 's/.*server\s(.+?):6379;.*/\1/p' /etc/nginx/nginx.conf) 

echo "[info] replacing $current with $0"

# If statement for extra credit:
if ["$current" -ne "$0"]; then
    # Replace the redis URL if the arg $0 url is different than $current
    sed -iE "s/server\s(.+?):6379/server $0:6379/" /etc/nginx/nginx.conf
    nginx -s reload
    echo "[info] successfully replaced redis url and reloaded nginx config"
else
    # Don't replace, instead print an error
    echo "[error] couldn't replace $current with $0 since they're the same url"
    exit 1
fi
