<h1> PUSH-STREAM </h1>

基于([FFCH4J](https://github.com/eguid/FFCH4J)) 修改

**项目说明** 
- 本项目使用spingboot开发
- 新增流地址CURD
- 加入websocker支持用户订阅，不订阅不推流

**环境需要** 
- ffmpeg
- nginx (windows 直接下载Gryphon版本，linux下载nginx-rtmp-module自行编译)

nginx 配置
```
worker_processes  2;

events {
    worker_connections  8192;
}

rtmp {
  server {
  
    listen 1935;
    
    #RTMP
    application rtmplive {
        live on;
    }
    
    #HLS
    application hls {
      live on;
      hls on;
      hls_path ./hls;
      hls_fragment 1s;
      #hls_playlist_length 3s;
    }
  }
}

http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        off;
    #tcp_nopush     on;

    server_names_hash_bucket_size 128;

    client_body_timeout   10;
    client_header_timeout 10;
    keepalive_timeout     30;
    send_timeout          10;
    keepalive_requests    10;

    #gzip  on;

    server {
        listen       80;
        server_name  localhost;

        location / {
            root   html;
            index  index.html index.htm;
        }
        
        # hls转m3u8
        location /hls{
            types {
                application/vnd.apple.mpegurl m3u8;
                video/mp2t ts;
            }
            alias ./hls/;
            add_header Cache-Control no-cache;
        }
    }
}

```


**项目截图** 
![Image text](https://github.com/yzcheng90/push-stream/blob/master/doc/1.png)
![Image text](https://github.com/yzcheng90/push-stream/blob/master/doc/2.png)
![Image text](https://github.com/yzcheng90/push-stream/blob/master/doc/3.png)
![Image text](https://github.com/yzcheng90/push-stream/blob/master/doc/4.png)
![Image text](https://github.com/yzcheng90/push-stream/blob/master/doc/5.png)


