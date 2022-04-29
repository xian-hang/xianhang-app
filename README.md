# 闲航 Android

#### Run backend server

```
python manage.py runserver 0.0.0.0:8000
```

#### Get your IP Address

run this command

```
ip a
# the inet IP is your Wlan IP
```

change the `BASE_URL` in `app/src/main/java/com/example/xianhang/network/ApiService.kt`

```
# Example
private const val BASE_URL = "http://192.168.0.117:8000/"
```

