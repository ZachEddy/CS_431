:- dynamic stench/2.
:- dynamic breeze/2.
:- dynamic wumpus/2.
:- dynamic pit/2.
:- dynamic wumpusDead/0.

%% Note for Adam: I couldn't figure out locate pits with 100% certainty.
%% Instead, I rely on knowing where a pit is already. My noPit(X,Y) checks
%% are incomplete (in terms of logical completeness). Wumpus(X,Y) and noWumpus(X,Y)
%% seem to work, though.

%% check east and west for wumpus
wumpus(X,Y) :- \+ wumpusDead(), North is Y + 1, South is Y - 1, stench(X,North), stench(X,South).

%% check north and south for wumpus
wumpus(X,Y) :- \+ wumpusDead(), East is X + 1, West is X - 1, stench(East,Y), stench(West,Y).

%% bottom left corner for wumpus
wumpus(1,1) :- stench(1,2), stench(2,1), \+ stench(2,3), \+ stench(3,2).

%% bottom right corner for wumpus
wumpus(4,1) :- stench(3,1), stench(4,2), \+ stench(3,3), \+ stench(2,2).

%% top left corner for wumpus
wumpus(1,4) :- stench(1,3), stench(2,4), \+ stench(3,3), \+ stench(2,2).

%% top right corner for wumpus
wumpus(4,4) :- stench(4,3), stench(3,4), \+ stench(2,3), \+ stench(3,2).


%% if the wumpus is dead, everywhere is safe
noWumpus(X,Y) :- wumpusDead(), assert(noWumpus(X,Y)).

%% if there is no stench around a given square, the wumpus isn't there
noWumpus(X,Y) :- North is Y + 1, South is Y - 1, East is X + 1, West is X - 1, \+ stench(X,North), \+ stench(X,South), \+ stench(Y,East), \+ stench(Y,West).

%% seems obvious, so I'm not sure it's necessary: if a wumpus is at a square, there is a wumpus at that square
noWumpus(X,Y) :- wumpus(X,Y),!, fail.

%% is there's a stench at a neighboring square of (X,Y), but no other stench is smelled around the same square (X,Y), there's not a wumpus at the square (X,Y)
noWumpus(X,Y) :- South is Y - 1, East is X + 1, West is X - 1, stench(X,South), \+ stench(East,Y), \+ stench(West,Y).

%% is there's a stench at a neighboring square of (X,Y), but no other stench is smelled around the same square (X,Y), there's not a wumpus at the square (X,Y)
noWumpus(X,Y) :- North is Y + 1, East is X + 1, West is X - 1, stench(X,North), \+ stench(East,Y), \+ stench(West,Y).

%% is there's a stench at a neighboring square of (X,Y), but no other stench is smelled around the same square (X,Y), there's not a wumpus at the square (X,Y)
noWumpus(X,Y) :- North is Y + 1, South is Y - 1, East is X + 1, stench(East,Y), \+ stench(X,North), \+ stench(X,South).

%% is there's a stench at a neighboring square of (X,Y), but no other stench is smelled around the same square (X,Y), there's not a wumpus at the square (X,Y)
noWumpus(X,Y) :- North is Y + 1, South is Y - 1, West is X - 1, stench(West,Y), \+ stench(X,North), \+ stench(X,South).

%% this checks to make sure there isn't a wumpus at a corner square. 
noWumpus(X,Y) :- North is Y + 1, East is X + 1, stench(X,North), stench(East,Y), wumpus(East,North).

%% this checks to make sure there isn't a wumpus at a corner square
noWumpus(X,Y) :- North is Y + 1, West is X - 1, stench(X,North), stench(West,Y), wumpus(West,North).

%% this checks to make sure there isn't a wumpus at a corner square. 
noWumpus(X,Y) :- South is Y - 1, East is X + 1, stench(X,South), stench(East,Y), wumpus(East,South).

%% this checks to make sure there isn't a wumpus at a corner square. 
noWumpus(X,Y) :- South is Y - 1, West is X - 1, stench(X,South), stench(West,Y), wumpus(West,South).


%% if there's a pit, then obviously nopit should fail
noPit(X,Y) :- pit(X,Y),!, fail.

%% if there's a breeze, you know there isn't a pit
noPit(X,Y) :- breeze(X,Y).

%% if there isn't a breeze around a given position, then you should the position doesn't have a pit
noPit(X,Y) :- North is Y + 1, South is Y - 1, East is X + 1, West is X - 1, \+ breeze(X,North), \+ breeze(X,South), \+ breeze(East,Y), \+ breeze(West,Y).

%% this checks to make sure a square is safe (no wumpus), (no pit)
isSafe(X,Y) :- noWumpus(X,Y), noPit(X,Y).