#!/bin/sh
set -e

export PORT="${PORT:-8080}"

upstream="${API_UPSTREAM:-http://app:8080}"
case "$upstream" in
  http://*|https://*) ;;
  *) upstream="http://$upstream" ;;
esac
export API_UPSTREAM="$upstream"

envsubst '${PORT} ${API_UPSTREAM}' \
  < /etc/nginx/templates/default.conf.template \
  > /etc/nginx/conf.d/default.conf

exec nginx -g 'daemon off;'
