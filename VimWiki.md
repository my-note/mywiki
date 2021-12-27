# VimWiki

## nginx

```
$ docker run --name wiki-nginx -v /home/user/Document/mywiki:/usr/share/nginx/html -d -p 80:80 --restart=always nginx
```
