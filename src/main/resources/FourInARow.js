// taken from https://github.com/BennySkidanov/bpjs-Benny/blob/master/4InARowVersion2.js

// Some sort of import 

/* Game Rules : 
 * 2 players - Red and Yellow, each has 21 round pieces in the color of the player. 
 * Lets assume, Yellow ( Computer ) and Red ( player ). 
 * A 6 x 7 board grid, which includes 42 positions for the round pieces mentioned earlier.
 * In an alternating order, each players chooses a column and releases a piece down the column. the piece lands and stays ( from that moment and on thorught the gam ) in the "lowest" 
 * possible position in the chosen column.
 * The goal of the game is to place 4 pieces in a row, column or diagonal. 
 * Strategies : 
 * 1. Of course, do not let the opponent win.
   2. Play the center column as much as possible.
   3. Create bottom 3 Trap.
 
 
 Wikipedia : The pieces fall straight down, occupying the lowest available space within the column. 
 The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. 
 Connect Four is a solved game. The first player can always win by playing the right moves.
 */ 
 
bp.log.info("Connect Four - Let's Go!!");
 
StaticEvents = {
	'RedWin':bp.Event("RedWin"),
	'YellowWin':bp.Event("YellowWin"),
	'Draw':bp.Event("Draw")
}; 

// Game ends when : 1. Either of the players has won ( Red or Yellow ) 2. It's a draw
bp.registerBThread("EndOfGame", function() {
	bp.sync({ waitFor:[ StaticEvents.RedWin, StaticEvents.YellowWin, StaticEvents.Draw ] });
	bp.sync({ block:[ moves ] });
});

bp.registerBThread("DetectDraw", function() {
	for (var i=0; i< 42; i++) { bp.sync({ waitFor:[ yellowCoinEs, redCoinEs ] }); }
	bp.sync({ request:[ StaticEvents.Draw ] }, 90);
});


var moves = bp.EventSet("Move events", function(e) {
	return e.name.startsWith("Put") || e.name.startsWith("Coin");
});


 
// put in column event 
function putInCol(col, color) {
	return bp.Event( "Put " + color +" (" + col + ")", {color:color, col:col});
}

// put coin in specific cell event 
function putCoin(row, col, color) {
	return bp.Event("Coin " + color + "(" + row + "," + col + ")", {color:color, row:row, col:col});
}

const redColES = bp.EventSet( "Red Col moves", function(evt){
    return evt.name.startsWith("Put Red");
});

const yellowColES = bp.EventSet( "Yellow Col moves", function(evt){
    return evt.name.startsWith("Put Yellow");
});

const redCoinEs = bp.EventSet( "Red Coin moves", function(evt){
    return evt.name.startsWith("Coin Red");
});

const yellowCoinEs = bp.EventSet( "Yellow moves", function(evt){
    return evt.name.startsWith("Coin Yellow");
});

const AnyPut = bp.EventSet("Any Put", function(evt) {
	return evt.name.startsWith("Put");
});

const col0ES =  bp.EventSet("Any Put 0", function(evt) {
	return evt.name.startsWith("Put") && evt.data.col == 0.0;
});
const col1ES =  bp.EventSet("Any Put 1", function(evt) {
	return evt.name.startsWith("Put") && evt.data.col == 1.0;
});
const col2ES =  bp.EventSet("Any Put 2", function(evt) {
	return evt.name.startsWith("Put") && evt.data.col == 2.0;
});
const col3ES =  bp.EventSet("Any Put 3", function(evt) {
	return evt.name.startsWith("Put") && evt.data.col == 3.0;
});
const col4ES =  bp.EventSet("Any Put 4", function(evt) {
	return evt.name.startsWith("Put") && evt.data.col == 4.0;
});
const col5ES =  bp.EventSet("Any Put 5", function(evt) {
	return evt.name.startsWith("Put") && evt.data.col == 5.0;
});
const col6ES =  bp.EventSet("Any Put 6", function(evt) {
	return evt.name.startsWith("Put") && evt.data.col == 6.0;
});


// req1: Represents alternating turns as mentioned in the game rules 
bp.registerBThread("EnforceTurns", function() {
	while (true) {
		bp.sync({ waitFor:yellowColES, block: [yellowCoinEs, redColES, redCoinEs, boardUpdatedES]});
		bp.sync({ waitFor:yellowCoinEs, block: [redColES, redCoinEs, yellowColES, boardUpdatedES]});
		bp.sync({ waitFor:boardUpdatedES, block: [redColES, redCoinEs, yellowColES, yellowCoinEs]});
		bp.sync({ waitFor:redColES, block: [yellowColES, yellowCoinEs, redCoinEs, boardUpdatedES]});
		bp.sync({ waitFor:redCoinEs, block: [redColES, yellowCoinEs, yellowColES, boardUpdatedES]});
		bp.sync({ waitFor:boardUpdatedES, block: [redColES, redCoinEs, yellowColES, redCoinEs]});
	}
});

//req2: physics - after put in a col, the coin falls to the first available place
bp.registerBThread("put in col" , function() { 
	let columns = [ 5 , 5 , 5 , 5 , 5 , 5 , 5 ];
	while(true) {
		let e = bp.sync({waitFor: AnyPut });
		bp.sync({request: putCoin( columns[e.data.col]-- , e.data.col, e.data.color)});
	}
});

//req3: one cannot put a coin in a full column
function blockPutInFullColumn(col){
	bp.registerBThread("one cannot put a coin in a full column " + col , function() { 
        let es = col == 0 ? col0ES :
        col == 1 ? col1ES : 
        col == 2 ? col2ES : 
        col == 3 ? col3ES : 
        col == 4 ? col4ES : 
        col == 5 ? col5ES :
        col == 6 ? col6ES : null ;

		for(let i = 0; i < 6; i++) {
            bp.sync({ waitFor: es });
        }
        while(true) {
		    bp.sync({ block: es });
        }
    })
}

let j = 0; 
for( j=0; j < 7; j++ ) 
{
	blockPutInFullColumn(j);
}

//req4: if a player places 4 coins in a line - the player wins
let allFours=[];


for(var i = 0; i < 3; i++ ) { 
	for(var j = 0; j < 4; j++) {
		allFours.push( [ { row : i, col : j } , { row : i, col : j+1 } , { row : i, col : j+2 } , { row : i, col : j+3 } ] );
		allFours.push( [ { row : i, col : j } , { row : i + 1, col : j } , { row : i + 2, col : j } , { row : i + 3, col : j } ] );
	}
}


for(var i = 3; i < 6; i++ ) { 
	for(var j = 0; j < 4; j++) {
        allFours.push( [ { row : i, col : j } , { row : i, col : j+1 } , { row : i, col : j+2 } , { row : i, col : j+3 } ] ); 
    }
}


for(var i = 0; i < 3; i++ ) { 
	for(var j = 4; j < 7; j++) {
        allFours.push( [ { row : i, col : j } , { row : i + 1, col : j } , { row : i + 2, col : j } , { row : i + 3, col : j } ] );
    }
}

for(var i = 0; i < 6; i++ ) { 
	for(var j = 0; j < 4; j++) {
		if( i <= 2 && j <= 3 ) {
			allFours.push( [ { row : i, col : j } , { row : i + 1, col : j+1 } , { row : i + 2, col : j+2 } , { row : i + 3, col : j+3 } ] );
		}
		else {
			allFours.push( [ { row : i, col : j } , { row : i - 1, col : j+1 } , { row : i - 2, col : j+2 } , { row : i - 3, col : j+3 } ] );
		}
	}
}


//rules for fours
let len = allFours.length; // number of fours 
for ( var i=0; i<len; i++ ) {
    (function(j){
        let currentFour = allFours[j];
        bp.registerBThread("Detect yellow win" + "[" + "(" +  currentFour[0].row + "," + currentFour[0].col + ")" + " ; " + 
        "(" +  currentFour[1].row + "," + currentFour[1].col + ")" + " ; " + 
        "(" +  currentFour[2].row + "," + currentFour[2].col + ")" + " ; " + 
        "(" +  currentFour[3].row + "," + currentFour[3].col + ")" + "]" , function() { 
            let fourEventArr = [];
            for(var i = 0; i < 4; i++) 
            {
                fourEventArr.push(putCoin(currentFour[i].row, currentFour[i].col, "Yellow"));
            }
    
            for ( var i=0; i<4; i++ ) {
                  bp.sync({waitFor:fourEventArr});
            }
            
            bp.sync({request:StaticEvents.YellowWin, block: moves }, 100);
        });
    })(i);
};


for ( var i=0; i<len; i++ ) {
    (function(j){
        let currentFour = allFours[j];
        bp.registerBThread("Detect red win" + "[" + "(" +  currentFour[0].row + "," + currentFour[0].col + ")" + " ; " + 
        "(" +  currentFour[1].row + "," + currentFour[1].col + ")" + " ; " + 
        "(" +  currentFour[2].row + "," + currentFour[2].col + ")" + " ; " + 
        "(" +  currentFour[3].row + "," + currentFour[3].col + ")" + "]" , function() { 
            let fourEventArr = [];
            for(var i = 0; i < 4; i++) 
            {
                fourEventArr.push(putCoin(currentFour[i].row, currentFour[i].col, "Red"));
            }
    
            for ( var i=0; i<4; i++ ) {
                  bp.sync({waitFor:fourEventArr});
            }
            
            bp.sync({request:StaticEvents.RedWin, block: moves }, 100);
        });
    })(i);
};




//From here: strategies

/*
bp.registerBThread("CenterCol", function() {
	while (true) {
		bp.sync({ request:[ putInCol(3, "Yellow"), 
							putInCol(3, "Red") ] });
	}
});

bp.registerBThread("semiCenterCol", function() {
	while (true) {
		bp.sync({ request:[  putInCol(1, "Yellow") , putInCol(2, "Yellow") , putInCol(4, "Yellow") , putInCol(5, "Yellow"), 
							 putInCol(1, "Red") , putInCol(2, "Red") , putInCol(4, "Red") , putInCol(5, "Red") ] });
	}
});

bp.registerBThread("sideCol", function() {
	while (true) {
		bp.sync({ request:[ putInCol(0, "Yellow"),putInCol(6, "Yellow"),
							putInCol(0, "Red"),putInCol(6, "Red")     ] });
	}
});
*/

bp.registerBThread('random yellow player', function() {
	const possiblePuts = Array.from(Array(7).keys()).map(j => putInCol(j, 'Yellow'))
	while(true) {
		let e = bp.sync({request: possiblePuts}, 10)
		// bp.log.info("random yellow requested: " + e)
	}
})

bp.registerBThread('random red player', function() {
	const possiblePuts = Array.from(Array(7).keys()).map(j => putInCol(j, 'Red'))
	while(true) {
		bp.sync({request: possiblePuts}, 10)
	}
})


bp.registerBThread("boardUpdater", function() {
	var board = [
		['*', '*','*', '*','*', '*','*'],

		['*', '*','*', '*','*', '*','*'],

		['*', '*','*', '*','*', '*','*'],

		['*', '*','*', '*','*', '*','*'],

		['*', '*','*', '*','*', '*','*'],

		['*', '*','*', '*','*', '*','*'],
	];

	for(var i=0; i < 42; i++) {
		var e = bp.sync({ waitFor:[ redCoinEs, yellowCoinEs ]});
		if(e.data.color.equals("Red")) 
		{
			let row = e.data.row;
			let col = e.data.col;
			board[row][col] = 'R';
		}
		else 
		{
			let row = e.data.row;
			let col = e.data.col;
			board[row][col] = 'Y'
		}

		bp.log.info("--------------------")
		for(var i = 0; i < 6; i++) {
			bp.log.info(board[i][0] + "  " + board[i][1] + "  " + board[i][2] + "  " + board[i][3] + "  " + board[i][4] + "  " + board[i][5] + "  " + board[i][6]);
		}
		bp.log.info(e.data)
		bp.log.info("--------------------")
		bp.sync({request: bp.Event("BoardUpdated", {board: board, ev: e})})
	}
});


var boardUpdatedES = bp.EventSet("BoardUpdated ES", function(e) {
	return e.name == "BoardUpdated";
});


bp.registerBThread("temp", function () {
	while (true) {
		let e = bp.sync({waitFor: boardUpdatedES})
		bp.log.info(e.data)
		// bp.log.info(e.data.board)
	}
})


//TODO
let series = [{row: 5, col: 2}, {row: 5, col: 3}, {row: 5, col: 4}]

/*
bp.registerBThread("seriesHandler", function() {
	let evs = []
	for (let i = 0; i < series.length; i++) {
		evs.push(frontierEv(series[i].row, series[i].col))
	}
	bp.sync({waitFor: evs});
	bp.log.info("seriesHandler caught")

	let requesting = []
	let ok = true
	for (var i = 0; i < series.length && ok; i++) {
		let cell = board[series[i].row][series[i].col]
		if (cell == 'R')
			ok = false
		else if (cell ='*')
			requesting.push(putInCol(series[i].col, "Yellow"))
	}

	//TODO loop
	if (ok) {
		bp.log.info("seriesHandler requesting " + requesting)
		var e = bp.sync({request: requesting}, 50)
		bp.log.info("seriesHandler requested " + e)
	}

})
*/