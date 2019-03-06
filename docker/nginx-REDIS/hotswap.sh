#!/usr/bin/env bash
set -e

# Set the nginx_config path:
nginx_config="/etc/nginx/nginx.conf"

# Check to make sure that an argument was passed in
if [[ -z "$1" ]]; then
    echo "[error] usage: hotswap.sh <new redis url>"
    exit 1
fi

# Get the current redis url:
current=$(sed -En 's/.*server\s(.+?):6379;.*/\1/p' $nginx_config) 


# If statement for extra credit:
if [[ "$current" != "$1" ]]; then
    echo "[info] replacing '$current' with '$1'"
    # Replace the redis URL if the arg $1 url is different than $current
    sed -i'' -E "s/server\s(.+?):6379/server $1:6379/" $nginx_config 
    nginx -s reload
    echo "[info] successfully replaced redis url and reloaded nginx config"
else
    # Don't replace, instead print an error
    echo "[error] couldn't replace url with '$1' because it's already set"
    exit 1
fi
