1. 用户对某个商品下单，系统创建订单成功，同时往延迟队列里put一个job。job结构为：{‘topic':'orderclose’, ‘id':'ordercloseorderNoXXX’, ‘delay’:1800 ,’TTR':60 , ‘body':’XXXXXXX’}

2. 延迟队列收到该job后，先往job pool中存入job信息，然后根据delay计算出绝对执行时间，并以轮询(round-robbin)的方式将job id放入某个bucket。

3. timer每时每刻都在轮询各个bucket，当1800秒（30分钟）过后，检查到上面的job的执行时间到了，取得job id从job pool中获取元信息。如果这时该job处于deleted状态，则pass，继续做轮询；如果job处于非deleted状态，首先再次确认元信息中delay是否大于等于当前时间，如果满足则根据topic将job id放入对应的ready queue，然后从bucket中移除；如果不满足则重新计算delay时间，再次放入bucket，并将之前的job id从bucket中移除。

4. 消费端轮询对应的topic的ready queue（这里仍然要判断该job的合理性），获取job后做自己的业务逻辑。与此同时，服务端将已经被消费端获取的job按照其设定的TTR，重新计算执行时间，并将其放入bucket。

5. 消费端处理完业务后向服务端响应finish，服务端根据job id删除对应的元信息。