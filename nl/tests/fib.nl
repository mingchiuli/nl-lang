fib = (n) => {
    if(n <= 1) {
         return 1;
    }else{
        return  fib(n-1) + fib(n-2);
    }
};
start = now();
println(fib(25));
println('cost',now()-start);