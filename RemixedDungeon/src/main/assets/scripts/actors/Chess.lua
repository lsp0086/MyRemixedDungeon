---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by mike.
--- DateTime: 2/20/23 12:36 AM
---

local RPD = require "scripts/lib/commonClasses"
local util = require "scripts/lib/util"
local actor = require "scripts/lib/actor"
local sunfish = require "scripts.stuff.chess.sunfish"

local x0 = 4
local y0 = 4

local pieces = {}
local rawPieces = {}

pieces_set = {
    ['r'] = 'Eye',
    ['R'] = 'Eye',
    ['n'] = 'Succubus',
    ['N'] = 'Succubus',
    ['b'] = 'Shaman',
    ['B'] = 'Shaman',
    ['Q'] = 'Warlock',
    ['q'] = 'Warlock',
    ['K'] = 'King',
    ['k'] = 'King',
    ['p'] = 'Rat',
    ['P'] = 'Rat'
}

chess = nil

local x_letters = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' }

local function chessCellFromCell(cell)
    local level = RPD.Dungeon.level

    x = level:cellX(cell) - x0
    y = level:cellY(cell) - y0

    if x >= 0 and x < 8 and y >= 0 and y < 8 then
        local chessCell = x_letters[x + 1] .. tostring(8 - y)
        --RPD.glog("inside of board %d -> %s", cell, chessCell)
        return chessCell
    end

    --RPD.glog("outside of board %d -> %d,%d", cell, x,y)
end

local function cellFromChessCell(chessCell)
    local level = RPD.Dungeon.level

    local x
    for i, letter in ipairs(x_letters) do
        if letter == chessCell:sub(1, 1) then
            x = i - 1
            break
        end
    end
    if not x then
        return nil
    end -- invalid chess cell
    local y = 8 - tonumber(chessCell:sub(2, 2))
    if y < 0 or y > 7 then
        return nil
    end -- invalid chess cell

    return level:cell(x + x0, y + y0)
end

local function cellFromChess(x, y)
    local level = RPD.Dungeon.level
    return level:cell(x + x0, y + y0 - 1)
end

local scheduledMoves = {}

local function movePiece(from, to)
    if RPD.Actor:motionInProgress() then
        table.insert(scheduledMoves, { from, to })
        return
    end

    local mob = RPD.Actor:findChar(from)
    local target = RPD.Actor:findChar(to)

    if not mob then
        error("mob not found on from cell " .. tostring(from))
    end

    if not target then
        mob:setPos(to)
        mob:moveSprite(from, to)
        return
    end

    if target then
        target:die(mob)
        mob:setPos(to)
        mob:moveSprite(from, to)
    end
end

local move_str = ''
local move_cells = {}
local chess_cells = {}

local function fillPiecesFromBoard()
    local boardData = util.split(chess.board, "\n")
    for i, v in ipairs(boardData) do
        if i >= 3 and i <= 10 then
            local y = i - 2

            local cell = cellFromChess(0, y)

            for ii = 1, 8 do
                local chessCell = chessCellFromCell(cell)
                local piece = string.sub(v, ii + 1, ii + 1)

                if pieces_set[piece] then
                    rawPieces[chessCell] = piece
                end
                cell = cell + 1
            end
        end
    end
end

local castleMovesList = { 'e1g1', 'e1c1', 'e8g8', 'e8c8' }
local rookMoves = { ['e1g1'] = 'h1f1', ['e1c1'] = 'a1d1',
                    ['e8g8'] = 'h8f8', ['e8c8'] = 'a8d8' }

local enPassantMovesList = { 'a4b3', 'b4a3', 'b4c3', 'c4b3', 'c4d3', 'd4c3', 'd4e3', 'e4d3', 'e4f3', 'f4e3', 'f4g3', 'g4f3', 'g4h3', 'h4g3',
                             'a5b6', 'b5a6', 'b5c6', 'c5b6', 'c5d6', 'd5c6', 'd5e6', 'e5d6', 'e5f6', 'f5e6', 'f5g6', 'g5f6', 'g5h6', 'h5g6' }
local pawnVictim = { ['a4b3'] = 'b4', ['b4a3'] = 'a4', ['b4c3'] = 'c4', ['c4b3'] = 'b4', ['c4d3'] = 'd4', ['d4c3'] = 'c4', ['d4e3'] = 'e4', ['e4d3'] = 'd4', ['e4f3'] = 'f4', ['f4e3'] = 'e4', ['f4g3'] = 'g4', ['g4f3'] = 'f4', ['g4h3'] = 'h4', ['h4g3'] = 'g4',
                     ['a5b6'] = 'b5', ['b5a6'] = 'a5', ['b5c6'] = 'c5', ['c5b6'] = 'b5', ['c5d6'] = 'd5', ['d5c6'] = 'c5', ['d5e6'] = 'e5', ['e5d6'] = 'd5', ['e5f6'] = 'f5', ['f5e6'] = 'e5', ['f5g6'] = 'g5', ['g5f6'] = 'f5', ['g5h6'] = 'h5', ['h5g6'] = 'g5' }

local castleMoves = {}
local enPassantMoves = {}

for _, v in ipairs(castleMovesList) do
    castleMoves[v] = true
end

for _, v in ipairs(enPassantMovesList) do
    enPassantMoves[v] = true
end

local function animateMove(move_str, move_cells, chess_cells)
    RPD.debug("animating: %s %s", chess_cells[1], chess_cells[2])
    movePiece(move_cells[1], move_cells[2])
    fillPiecesFromBoard()

    if castleMoves[move_str] then
        --RPD.glog("check for castle: %s %s", move_str, chess_cells[2])
        --RPD.glog("check for castle: %s %s", rawPieces[chess_cells[1]], rawPieces[chess_cells[2]])
        if rawPieces[chess_cells[2]] == 'K' or rawPieces[chess_cells[2]] == 'k' then
            local rookMove = rookMoves[move_str]
            local cells = { cellFromChessCell(string.sub(rookMove, 1, 2)),
                            cellFromChessCell(string.sub(rookMove, 3, 4)) }
            movePiece(cells[1], cells[2])
        end
    end

    if enPassantMoves[move_str] then
        RPD.glog("check en passant: %s %s", move_str, chess_cells[2])
        local pawnCell = pawnVictim[move_str]
        if rawPieces[pawnCell] == 'P' or rawPieces[pawnCell] == 'p' then
            local cell = cellFromChessCell(pawnCell)
            local mob = RPD.Actor:findChar(cell)
            if mob then
                mob:die()
            end
        end
    end
end

local function highlightCells(cells)
    RPD.Sfx.HighlightCell:removeAll()

    for k, cell in pairs(cells) do
        local pos = cellFromChessCell(cell)
        RPD.Sfx.HighlightCell:add(pos)
    end
end

local gameInProgress = true

local function processLose()
    RPD.glog("You Lose!")

    RPD.Dungeon.hero:die(RPD.Dungeon.hero)

    gameInProgress = false
end

local function processWin()
    local mobs = RPD.Dungeon.level.mobs

    local iterator = mobs:iterator()

    local mobsToDie = {}

    while iterator:hasNext() do
        local mob = iterator:next()
        table.insert(mobsToDie, mob)
    end

    for i,mob in ipairs(mobsToDie) do
        mob:die(RPD.Dungeon.hero)
    end

    RPD.glog("You Win!")
    gameInProgress = false
end

local moveDelay = 5
local allowedMoves = {}

local function getPiece(board, engineCell)
    return string.sub(board, engineCell+1, engineCell + 1)
end

return actor.init({
    act = function()
        if #scheduledMoves > 0 and not RPD.Actor:motionInProgress() then
            if moveDelay > 0 then
                moveDelay = moveDelay - 1
            else
                moveDelay = 5

                local move = scheduledMoves[1]
                movePiece(move[1], move[2])
                table.remove(scheduledMoves, 1)
            end
        end
        return true
    end,

    actionTime = function()
        return 0.1
    end,

    activate = function()
        chess = sunfish.new()
        pieces = {}
        local level = RPD.Dungeon.level

        local boardData = util.split(chess.board, "\n")
        for i, v in ipairs(boardData) do
            print(v)
            if i >= 3 and i <= 10 then
                local y = i - 2

                local cell = cellFromChess(0, y)

                for ii = 1, 8 do
                    local chessCell = chessCellFromCell(cell)

                    local piece = string.sub(v, ii + 1, ii + 1)

                    if pieces_set[piece] then
                        if not pieces[chessCell] or pieces[chessCell]:getEntityKind() ~= pieces_set[piece] then
                            local mob = RPD.MobFactory:mobByName(pieces_set[piece])

                            mob:setPos(cell)
                            RPD.setAi(mob, "PASSIVE")
                            level:spawnMob(mob)

                            if piece == piece:upper() then
                                mob:lightness(0.6)
                            else
                                mob:lightness(0.4)
                            end

                            pieces[chessCell] = mob
                        end
                    end

                    --RPD.glog("%s -> %s", chessCell, piece)
                    cell = cell + 1
                end

                --RPD.glog('\n')

            end

        end

    end,

    cellClicked = function(cell)

        if not gameInProgress then
            return false
        end

        chessCell = chessCellFromCell(cell)
        engineCell = sunfish.cell_2_move(chessCell)

        if not chessCell then
            RPD.glog("it is your move, Hero")
            return false
        end

        RPD.Sfx.HighlightCell:removeAll()

        if string.len(move_str) == 0 then
            move_cells[1] = cell
            chess_cells[1] = chessCell
            move_str = chessCell

            allowedMoves = {}

            local pseudoMoves = chess:genMoves()
            local moveCandidates = {}

            for i, v in ipairs(pseudoMoves) do

                if sunfish.move_2_cell(v[1]) == chessCell then

                    local validMove = true

                    local move_str = sunfish.move_2_cell(v[1]) .. sunfish.move_2_cell(v[2])
                    RPD.debug("checking move: %s", move_str)
                    RPD.debug("own piece: %d %d %s %s", v[1], v[2],
                            getPiece(chess.board, v[1]),
                            getPiece(chess.board, v[2]))

                    probe = sunfish.move(chess, move_str)

                    probeMoves = probe:genMoves()

                    for ii, vv in ipairs(probeMoves) do
                        local target = getPiece(probe.board,vv[2])
                        local move_str = sunfish.move_2_cell(vv[1]) .. sunfish.move_2_cell(vv[2])

                        RPD.debug("ai piece: %d %d %s %s %s",vv[1],vv[2], getPiece(probe.board, vv[1]), target, move_str)
                        if target == 'k' then
                            validMove = false
                        end
                    end

                    if validMove then
                        RPD.debug("valid")
                        allowedMoves[sunfish.move_2_cell(v[2])] = true
                        table.insert(moveCandidates, v)
                    else
                        RPD.debug("invalid")
                    end
                end
            end

            local hCells = {}
            for i, candidate in ipairs(moveCandidates) do
                table.insert(hCells, sunfish.move_2_cell(candidate[2]))
            end

            highlightCells(hCells)
        else

            clickedPiece = getPiece(chess.board, engineCell)
            if string.match(clickedPiece,"%u") then
                move_str = ''
                RPD.glog("another piece clicked: %s", clickedPiece)
                return true
            end

            if not allowedMoves[chessCell] then
                RPD.glog("illegal move: %s", chessCell)
                return true
            end

            move_str = move_str .. chessCell
            move_cells[2] = cell
            chess_cells[2] = chessCell

            local moveResult = sunfish.move(chess, move_str)

            if moveResult then
                chess = moveResult:rotate()
                RPD.glog("your move Position score: %s", chess.score)

                RPD.Sfx.HighlightCell:removeAll()
                animateMove(move_str, move_cells, chess_cells)

                moveResult:rotate()
                chess, ai_move, score = sunfish.ai_move(chess:rotate())

                if score <= -sunfish.MATE_VALUE then
                    processWin()
                    return true
                end

                if score >= sunfish.MATE_VALUE then
                    processLose()
                    return true
                end

                local chess_cells = { string.sub(ai_move, 1, 2), string.sub(ai_move, 3, 4) }

                ai_cells = { cellFromChessCell(chess_cells[1]), cellFromChessCell(chess_cells[2]) }

                animateMove(ai_move, ai_cells, chess_cells)
                RPD.glog("ai move Position score: %s", chess.score)

            else
                RPD.glog('illegal move')
            end
            move_str = ''
        end

        return true
    end
})