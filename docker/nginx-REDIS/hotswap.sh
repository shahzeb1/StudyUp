#!/usr/bin/env bash
set -e

# Get the current redis url:
current=$(sed -En 's/.*server\s(.+?):6379;.*/\1/p' /etc/nginx/nginx.conf) 

echo "[info] replacing $current with $0"

# If statement for extra credit:
if ["$current" -ne "$0"]
    # Replace the redis URL if the arg $0 url is different than $current
    sed -iE "s/server\s(.+?):6379/server $0:6379/" /etc/nginx/nginx.conf
    nginx -s reload
    echo "[info] successfully replaced redis url and reloaded nginx config"
else
    # Don't replace, instead print an error
    echo "[error] couldn't replace $current with $0 since they're the same url"
fi
