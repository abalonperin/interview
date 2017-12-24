# Technical stack:
* HTTP clint: [sttp](https://github.com/softwaremill/sttp)
* Simple refinement types for Scala: [refined
](https://github.com/fthomas/refined)
* Cahce: [scaffeine
](https://github.com/blemale/scaffeine) 

# How do I design this service?
According to requirements:
> An internal user of the application should be able to ask for an exchange rate between 2 given currencies, and get back a rate that is not older than 5 minutes. The application should at least support 10.000 requests per day.
?

I need to obsolete a rate which is older than 5 minutes and contend with heavy traffic.
A simple solution is that I access 1-forge service once when a user triggers a request and get rid of out-of-date rate.
There are several disadvantages: 
1. when traffic becomes heavy, there is a high possibility that I  might break 1-forge service and forex service.
2. when traffic becomes heavy, It might cause the tremendous cost of 1-forge service because 1-forge service API isn't free.
3. when an internet connection is poor,  forex service might always return internal server error or the response time might become longer.

In order to not to access 1-forge service every time, I assume a currency rate should not have a dramatic change in 5 minutes. 
So I put a rate that gets from 1-forge service into a cache and this rate will expire after 5 minutes. 
According to this design, I don't frequently reach 1-forge service. 
It improves the response time and reduces the expense of 1-forge service and forex service have a capacity of serving more online users.

## Architecture
![architecture](https://www.dropbox.com/s/75uaavfthwyg1ni/Forex.jpg?dl=0)   
                    
# How to run all test cases?
```scala
sbt test
```

# How to access this service?
1. start service
```scala
sbt run
```

2. access service
```bash
curl 'http://localhost:8888?from=USD&to=JPY'
* Rebuilt URL to: http://localhost:8888/?from=USD&to=JPY
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to localhost (127.0.0.1) port 8888 (#0)
> GET /?from=USD&to=JPY HTTP/1.1
> Host: localhost:8888
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 200 OK
< Server: akka-http/10.0.10
< Date: Fri, 22 Dec 2017 09:56:54 GMT
< Content-Type: application/json
< Content-Length: 77
<
* Connection #0 to host localhost left intact
{"from":"USD","to":"JPY","price":113.3465,"timestamp":"2017-12-22T09:56:48Z"}

curl 'http://localhost:8888?from=USD&to=XXX'
* Rebuilt URL to: http://localhost:8888/?from=USD&to=XXX
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to localhost (127.0.0.1) port 8888 (#0)
> GET /?from=USD&to=XXX HTTP/1.1
> Host: localhost:8888
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 400 Bad Request
< Server: akka-http/10.0.10
< Date: Fri, 22 Dec 2017 16:15:43 GMT
< Content-Type: text/plain; charset=UTF-8
< Content-Length: 71
<
The query parameter 'to' was malformed:
* Connection #0 to host localhost left intact
XXX (of class java.lang.String)

curl -v 'http://localhost:8888?from=SGD&to=CAD'
* Rebuilt URL to: http://localhost:8888/?from=SGD&to=CAD
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to localhost (127.0.0.1) port 8888 (#0)
> GET /?from=SGD&to=CAD HTTP/1.1
> Host: localhost:8888
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 500 Internal Server Error
< Server: akka-http/10.0.10
< Date: Fri, 22 Dec 2017 16:38:50 GMT
< Content-Type: text/plain; charset=UTF-8
< Content-Length: 166
<
* Connection #0 to host localhost left intact
{"reason":"forger service error: We are unable to convert the price for the given currencies.  If you need help, please email contact@1forge.com", "throwable":"None"}
```

# Improvement
1. The body of error response:
Should transform body of error response into json format.

2. The `Content-Type` of error response:
Should transform `Content-Type` of error response into `application/json`.

3. A monitor mechanism
Should send events to a monitor system in order to track this service. 
