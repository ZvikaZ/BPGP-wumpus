TODO
====

NEXT
----
- improve grammar:
  
```js
// what about these events?
var moves = bp.EventSet("Move events", function(e) {
    return e.name.startsWith("Put") || e.name.startsWith("Coin");
});

// put coin in specific cell event 
function putCoin(row, col, color) {
    return bp.Event("Coin " + color + "(" + row + "," + col + ")", {color:color, row:row, col:col});
}

// what about this construct??
bp.registerBThread("put in col" , function() {
    let columns = [ 5 , 5 , 5 , 5 , 5 , 5 , 5 ];
    while(true) {
        let e = bp.sync({waitFor: AnyPut });
        bp.sync({request: putCoin( columns[e.data.col]-- , e.data.col, e.data.color)});
    }
});


```

- bpjs verification?
- parallelize
- coevolution of both sides
- sensible initialization
- print seed -> to screen, and to stats

LATER
-----
- print genome, derivation
- play against preknown good program
- maven standalone on clean copy 
- some explanations in this file...
- clean ecj warnings
- unit tests?

