# d - delay, c - concurrency, v - verbose (print messages), t - time for testing

#siege -d1  -c50  -v -t15s http://localhost:8080/select-equals
#siege -d1  -c50  -v -t15s http://localhost:8080/select-from-to
#siege -d1  -c50  -v -t15s http://localhost:8080/select-in

siege -d1  -c100  -v -t60s http://localhost:8080/insert