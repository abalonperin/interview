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
2. when traffic becomes heavy, It might raise the tremendous cost of 1-forge service if we use 1-forge paid API.
3. when an internet connection is poor of 1-forge service has longer response time, forex service might always return internal server error or the response time might become longer.

In order to not to access 1-forge service every time, I assume a currency rate should not have a dramatic change in 5 minutes. 
So I put a rate that gets from 1-forge service into a cache and this rate will expire after 5 minutes. 
According to this design, I don't frequently reach 1-forge service. 
It improves the response time and reduces the expense of 1-forge service and forex service have a capacity of serving more online users.

## Architecture
https://www.dropbox.com/s/4ewns54j3hls0hh/Forex.jpg?dl=0
                    
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
```

# How to access Swagger?

1. start service
```scala
sbt run
```

2. open your browser and type `http://localhost:8888/swagger?url=http://localhost:8888/api-docs/swagger.json`

# Improvement
* A monitor mechanism
Should send events to a monitor system in order to track this service.
