param($DEBUG, $LEAGUE)

$LEAGUE1 = $LEAGUE -eq 1
$LEAGUE2 = $LEAGUE -eq 2
$LEAGUE3 = $LEAGUE -eq 3
$USER_LEAGUE = $LEAGUE

function section($league, $content, $else="") {
    if($USER_LEAGUE -ge  $league) {
        Write-Output $content
    } else {
        Write-Output $else;
    }
}

function newSection($league, $content, $else=""){
    if($league -eq $USER_LEAGUE ) {
        Write-Output "<div class='statement-summary-new-league-rules'>$content</div>"
    } elseif ($USER_LEAGUE -gt $league){
        Write-Output $content
    } else {
        Write-Output $else;
    }
}

if($DEBUG){
"
<html>
<head>
    <link href='codeingame-mock.css' rel='stylesheet'>
</head>
<body style='font-family:Open Sans,Lato,sans-serif!important'>
"
}

"
<div class='statement-body'>
    <!-- GOAL -->
    <div class='statement-section statement-goal'>
        <h2>
            <span class='icon icon-goal'>&nbsp;</span>
            <span>The Goal</span>
        </h2>
        <img class='statement-goal-content'>
        <p>
            Hit your opponent and manage our energy to score 20 points!
        </p>
    </div>
"

if($LEAGUE1){
        "<div class='statement-summary-new-league-rules'>
    <p>new rules added in the league 2 and league 3: ignore the variables marked for the next leagues.</p>
    </div>"
    } elseif($LEAGUE2){
        "<div class='statement-summary-new-league-rules'>
    <p>read new rules in the green sections.</p>
    </div>"
    } elseif($LEAGUE3){
        "
    <div class='statement-summary-new-league-rules'>
        <p>Last league with new rules: read new rules in the green sections.</p>
    </div>
    "
    }

    "    <div class='statement-victory-conditions'>
        <div class='icon victory'></div>
        <div class='blk'>
            <div class='title'>Victory Conditions</div>
            <div class='text'>
                <ul style='padding-bottom:0'>
                    <li>Score more points than your opponent(s) at games end.</li>
                    <li>Decisive victory when you score 20 points at least 2 points apart.</li>
                </ul>
            </div>
        </div>
    </div>
    <div class='statement-lose-conditions'>
        <div class='icon lose'></div>
        <div class='blk'>
            <div class='title'>Loss Conditions</div>
            <div class='text'>
                <ul style='padding-bottom:0'>
                    <li>Score fewer points than your opponent(s) at games end.</li>
                    <li>In some cases you loses and receives an additional ranking penalty:</li>
                    <ul>
                        <li>forfeit: when you run out of energy.</li>
                        <li>non-combativity: when you and the opponent don't score any point at games end (200
                            frames).
                        </li>
                        <li>issue: you do not respond in due time (50ms) or output an invalid command.</li>
                        <li>doping: when you abuse of drugs (more than seven)</li>
                    </ul>
                </ul>
            </div>
        </div>
    </div>
    <!-- RULES -->
    <div class='statement-section statement-rules'>
        <h2>
            <span class='icon icon-rules'>&nbsp;</span>
            <span>Rules</span>
        </h2>
        <div>
            <div class='statement-rules-content'>
                <h3>Actions</h3>
                <p><br>Two players choose simultaneously only one action:</p>
                <ul>
                    <li>
                        <action>BREAK</action>
                        : energy=
                        <const>+2</const>
                    </li>
                    <li>
                        <action>WALK</action>
                        : energy=
                        <const>-1</const>
                        move=
                        <const>+20</const>
                    </li>
                    <li>
                        <action>RETREAT</action>
                        : energy=
                        <const>-1</const>
                        move=
                        <const>-20</const>
                    </li>
                    <li>
                        <action>LUNGE</action>
                        : energy=
                        <const>-5</const>
                        distance=
                        <const>+40</const>
                    </li>
                    <li>
                        <action>PARRY</action>
                        : energy=
                        <const>-2</const>
                        distance=
                        <const>-40</const>
                        energyTransfer=
                        <const>2</const>
                    </li>
                </ul>
                "
newSection -league 2 -content "
                <ul>
                    <li>
                        <action>DOUBLE_WALK</action>
                        : energy=
                        <const>-4</const>
                        move=
                        <const>+40</const>
                    </li>
                    <li>
                        <action>DOUBLE_RETREAT</action>
                        : energy=
                        <const>-4</const>
                        move=
                        <const>-30</const>
                    </li>
                </ul>
"

newSection -league 3 -content "
                <ul>
                    <li>
                        <action>LUNGE_DRUG</action>
                        : energy=
                        <const>-5</const>
                        LungeSkill=
                        <const>+5</const>
                    </li>
                    <li>
                        <action>PARRY_DRUG</action>
                        : energy=
                        <const>-5</const>
                        ParrySkill=
                        <const>-5</const>
                    </li>
                    <li>
                        <action>ENERGY_MAX_DRUG</action>
                        : energy=
                        <const>-5</const>
                        EnergyMax=
                        <const>+5</const>
                    </li>
                    <li>
                        <action>WALK_DRUG</action>
                        : energy=
                        <const>-5</const>
                        WalkSkill=
                        <const>+5</const>
                    </li>
                    <li>
                        <action>RETREAT_DRUG</action>
                        : energy=
                        <const>-5</const>
                        RetreatSkill=
                        <const>+5</const>
                    </li>
                    <li>
                        <action>DOUBLE_WALK_DRUG</action>
                        : energy=
                        <const>-5</const>
                        DoubleWalkSkill=
                        <const>+10</const>
                    </li>
                    <li>
                        <action>DOUBLE_RETREAT_DRUG</action>
                        : energy=
                        <const>-5</const>
                        DoubleRetreatSkill=
                        <const>+10</const>
                    </li>
                    <li>
                        <action>BREAK_DRUG</action>
                        : energy=
                        <const>-5</const>
                        BreakSkill=
                        <const>+10</const>
                    </li>
                </ul>
                "

    "
                <p>let me explain the behavior of each property:</p>
                "
newSection -league 3 -content "
<ul>
                    <li>energy: consume and produce in the range [<const>0</const>,<var>Player.EnergyMax</var>].
                    </li>
</ul>" -else "
<ul>
                    <li>energy: consume and produce in the range [<const>0</const>,<const>20</const>]
                        ).
                    </li>
</ul>
"

    "
<ul>
                    <li>move: apply a move of your character in the range [
                        <const>0</const>
                        ,
                        <const>500</const>
                        ].
                    </li>
                    <li>distance: positive is an attack; negative is a defense; see &sect;Assault.</li>
                    <li>energyTransfer: transfer the quantity from your opponent to you.</li>
                </ul>"
newSection -league 3 -content "
                <ul>
                    <li>EnergyMax: increase the energy gauge!</li>
                    <li>BreakSkill: increase your recovery; added to break's energy .</li>
                    <li>LungeSkill: added to the lunge's distance.</li>
                    <li>ParrySkill: added to the parry's distance.</li>
                    <li>WalkSkill: added to walk's move.</li>
                    <li>RetreatSkill: added to retreat's move.</li>
                    <li>DoubleWalkSkill: added to double walk's move.</li>
                    <li>DoubleRetreatSkill: added to double retreat's move.</li>
                </ul>
"

    "                <h3>Resolutions</h3>

                <ul>
                    <li>the action's energy is consumed or produced in any cases</li>
                    <li>You declare forfeit when you run out of energy.</li>
                    <li>The players respawn when:
                        <ul>
                            <li>they collide (<var>myPosition</var> > <var>yourPosition</var>)
                            <li>an assault is succeeded.</li>
                            <li>a player is outside.</li>
                        </ul>
                    <li>the opponent score one point when you are moved outside of the piste.</li>

                    <li>The players score both when they touch simultaneously (
                        <action>LUNGE</action>
                        vs
                        <action>LUNGE</action>
                        are resolved).
                    </li>
                </ul>
                <h3>Assaults</h3>
                "
                if($LEAGUE3){
                "<div class='statement-summary-new-league-rules'>
                        <p>Don't forget! change the formula to handle <var>lungeDistanceSkill</var> and <var>parryDistanceSkill</var>..</p>
                    </div>"
                }
                "
                <p>
                    <br>Assault resolution depends on the positions and distances of the actions:
                </p>
                <div style='background-color:#555;color:white;font-family:monospace;padding:1em'>
                    <p>isTouchedWhenLunge(striker:Player, defender:Player) ->
                        Abs(striker.position - defender.position) >= striker.lungeDistance;
                    </p>
                    <p>isTouchedWhenLungeParry(striker:Player, defender:Player) ->
                        Abs(striker.position - defender.position) >= striker.lungeDistance + defender.parryDistance;</p>
                </div>

                <p>to use it, two samples bellow:</p>
                <div style='background-color:#555;color:white;font-family:monospace;padding:1em'>
                    <p>ILungeAndOpponentDefends = isTouchedWhenLungeParry(me, opponent)</p>
                    <p>OpponentLunges = isTouchedWhenLunge(opponent, me)</p>
                </div>
            </div>
        </div>
    </div>
    <!-- PROTOCOL -->
    <div class='statement-section statement-protocol'>
        <h2>
            <span class='icon icon-protocol'>&nbsp;</span>
            <span>Game I/O</span>
        </h2>
        <!-- Protocol block -->
        <div class='blk'>
            <div class='title'>Input for one game turn</div>
            <div class='text'>
                <span class='statement-lineno'>Line 1, my data:</span>
                <var>position</var>&nbsp;<var>energy</var>&nbsp;<var>score</var>&nbsp;<var>drugCount</var>&nbsp;<var>energyMax</var>&nbsp;<var>breakSkill</var>&nbsp;<var>walkSkill</var>&nbsp;<var>doubleWalkSkill</var>&nbsp;<var>retreatSkill</var>&nbsp;<var>doubleRetreatSkill</var>&nbsp;<var>lungeDistanceSkill</var>&nbsp;<var>parryDistanceSkill</var>
            </div>
            <br/>
            <div class='text'>
                <span class='statement-lineno'>Line 2, opponent data:</span>
                <var>position</var>&nbsp;<var>energy</var>&nbsp;<var>score</var>&nbsp;<var>drugCount</var>&nbsp;<var>energyMax</var>&nbsp;<var>breakSkill</var>&nbsp;<var>walkSkill</var>&nbsp;<var>doubleWalkSkill</var>&nbsp;<var>retreatSkill</var>&nbsp;<var>doubleRetreatSkill</var>&nbsp;<var>lungeDistanceSkill</var>&nbsp;<var>parryDistanceSkill</var>
            </div>
            <div class='text'>
                <ul>
                    <li>
                        <var>position</var>: range [
                        <const>0</const>
                        ,
                        <const>500</const>
                        ], respawn (me:
                        <const>200</const>
                        , opponent:
                        <const>300</const>
                        )
                    </li>
                    <li>
                        <var>energy</var>: range [
                        <const>0</const>
                        ,
                        $(section -league 3 -content '<var>player.energyMax</var>' -else '<const>20</const>')
                        ], init
                        <const>20</const>
                    </li>
                    <li>
                        <var>score</var>: range [
                        <const>0</const>
                        ,
                        <const>+&infin;</const>
                        ], init
                        <const>0</const>
                    </li>
                </ul>
"

newSection -league 3 -content "
                <ul>
                    <li>
                        <var>drugCount</var>:you can consume a maximum of 7 drugs; range [
                        <const>0</const>
                        ,
                        <const>7</const>
                        ]; init
                        <const>0</const>
                    </li>
                    <li>
                        <var>energyMax</var>: init
                        <const>20</const>
                        ; increased by
                        <action>ENERGY_MAX_DRUG</action>
                    </li>
                    <li>
                        <var>breakSkill</var>: increase your recovery; added to break's energy; increased by
                        <action>BREAK_DRUG</action>
                    </li>
                    <li>
                        <var>walkSkill</var>: added to walk's move; increased by
                        <action>WALK_DRUG</action>
                    </li>
                    <li>
                        <var>doubleWalkSkill</var>: added to double walk's move;increased by
                        <action>DOUBLE_WALK_DRUG</action>
                    </li>
                    <li>
                        <var>retreatSkill</var>: added to retreat's move; increased by
                        <action>RETREAT_DRUG</action>
                    </li>
                    <li>
                        <var>doubleRetreatSkill</var>: added to double retreat's move; increased by
                        <action>DOUBLE_RETREAT_DRUG</action>
                    </li>
                    <li>
                        <var>lungeDistanceSkill</var>:added to the lunge's distance;increased by
                        <action>LUNGE_DRUG</action>
                    </li>
                    <li>
                        <var>parryDistanceSkill</var>: added to the parry's distance;increased by
                        <action>PARRY_DRUG</action>
                    </li>
                </ul>"

"
            </div>
        </div>
        <!-- Protocol block -->
        <div class='blk'>
            <div class='title'>Output for one game turn</div>
            <div class='text'>
                <span class='statement-lineno'>Line 1:</span>
                <action>myAction</action>
            </div>
            <div class='text'>
                possibles values:
                <ul>
                    <li>
                        <action>BREAK</action>
                        : energy=
                        <const>+2</const>
                    </li>
                    <li>
                        <action>WALK</action>
                        : energy=
                        <const>-1</const>
                        move=
                        <const>+20</const>
                    </li>
                    <li>
                        <action>RETREAT</action>
                        : energy=
                        <const>-1</const>
                        move=
                        <const>-20</const>
                    </li>
                    <li>
                        <action>LUNGE</action>
                        : energy=
                        <const>-5</const>
                        distance=
                        <const>+40</const>
                    </li>
                    <li>
                        <action>PARRY</action>
                        : energy=
                        <const>-2</const>
                        distance=
                        <const>-40</const>
                        energyTransfer=
                        <const>2</const>
                    </li>
                </ul>"

newSection -league 2 -content "
                <ul>
                    <li>
                        <action>DOUBLE_WALK</action>
                        : energy=
                        <const>-4</const>
                        move=
                        <const>+40</const>
                    </li>
                    <li>
                        <action>DOUBLE_RETREAT</action>
                        : energy=
                        <const>-4</const>
                        move=
                        <const>-30</const>
                    </li>
                </ul>"

newSection -league 3 -content "
                <ul>
                    <li>
                        <action>LUNGE_DRUG</action>
                        : energy=
                        <const>-5</const>
                        LungeSkill=
                        <const>+5</const>
                    </li>
                    <li>
                        <action>PARRY_DRUG</action>
                        : energy=
                        <const>-5</const>
                        ParrySkill=
                        <const>-5</const>
                    </li>
                    <li>
                        <action>ENERGY_MAX_DRUG</action>
                        : energy=
                        <const>-5</const>
                        EnergyMax=
                        <const>+5</const>
                    </li>
                    <li>
                        <action>WALK_DRUG</action>
                        : energy=
                        <const>-5</const>
                        WalkSkill=
                        <const>+5</const>
                    </li>
                    <li>
                        <action>RETREAT_DRUG</action>
                        : energy=
                        <const>-5</const>
                        RetreatSkill=
                        <const>+5</const>
                    </li>
                    <li>
                        <action>DOUBLE_WALK_DRUG</action>
                        : energy=
                        <const>-5</const>
                        DoubleWalkSkill=
                        <const>+10</const>
                    </li>
                    <li>
                        <action>DOUBLE_RETREAT_DRUG</action>
                        : energy=
                        <const>-5</const>
                        DoubleRetreatSkill=
                        <const>+10</const>
                    </li>
                    <li>
                        <action>BREAK_DRUG</action>
                        : energy=
                        <const>-5</const>
                        BreakSkill=
                        <const>+10</const>
                    </li>
                </ul>"

"
            </div>
        </div>
    </div>
    <div class='statement-section statement-rules'>
        <h2><span class='icon icon-rules'>&nbsp;</span><span>Raw Data</span></h2>
        <table class='marked'>
            <tr>
                <th>code</th>
                <th>energy</th>
                <th>energy transfer</th>
                <th>move</th>
                <th>distance</th>
                <th>drug(league3)</th>
                <th>league</th>
            </tr>
            <tr>
                <td>
                    <action>BREAK</action>
                </td>
                <td>
                    <const>
                        <constant>+2</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>1</td>
            </tr>
            <tr>
                <td>
                    <action>WALK</action>
                </td>
                <td>
                    <const>
                        <constant>-1</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                </td>
                <td>
                    <const>
                        <constant>+20</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>1</td>
            </tr>
            <tr>
                <td>
                    <action>RETREAT</action>
                </td>
                <td>
                    <const>
                        <constant>-1</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                </td>
                <td>
                    <const>
                        <constant>-20</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>1</td>
            </tr>
            <tr>
                <td>
                    <action>LUNGE</action>
                </td>
                <td>
                    <const>
                        <constant>-5</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>+40</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>1</td>
            </tr>
            <tr>
                <td>
                    <action>PARRY</action>
                </td>
                <td>
                    <const>
                        <constant>-2</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>2</constant>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>-40</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>1</td>
            </tr>
"

section -league 2 -content "
            <tr>
                <td>
                    <action>DOUBLE_WALK</action>
                </td>
                <td>
                    <const>
                        <constant>-4</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                </td>
                <td>
                    <const>
                        <constant>+40</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>2</td>
            </tr>
            <tr>
                <td>
                    <action>DOUBLE_RETREAT</action>
                </td>
                <td>
                    <const>
                        <constant>-4</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                </td>
                <td>
                    <const>
                        <constant>-30</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>2</td>
            </tr>"

section -league 3 -content "
            <tr>
                <td>
                    <action>LUNGE_DRUG</action>
                </td>
                <td>
                    <const>
                        <constant>-5</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>+5</constant>
                    </const>
                </td>
                <td>3</td>
            </tr>
            <tr>
                <td>
                    <action>PARRY_DRUG</action>
                </td>
                <td>
                    <const>
                        <constant>-5</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>-5</constant>
                    </const>
                </td>
                <td>3</td>
            </tr>
            <tr>
                <td>
                    <action>ENERGY_MAX_DRUG</action>
                </td>
                <td>
                    <const>
                        <constant>-5</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>+5</constant>
                    </const>
                </td>
                <td>3</td>
            </tr>
            <tr>
                <td>
                    <action>WALK_DRUG</action>
                </td>
                <td>
                    <const>
                        <constant>-5</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>+5</constant>
                    </const>
                </td>
                <td>3</td>
            </tr>
            <tr>
                <td>
                    <action>RETREAT_DRUG</action>
                </td>
                <td>
                    <const>
                        <constant>-5</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>+5</constant>
                    </const>
                </td>
                <td>3</td>
            </tr>
            <tr>
                <td>
                    <action>DOUBLE_WALK_DRUG</action>
                </td>
                <td>
                    <const>
                        <constant>-5</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>+10</constant>
                    </const>
                </td>
                <td>3</td>
            </tr>
            <tr>
                <td>
                    <action>DOUBLE_RETREAT_DRUG</action>
                </td>
                <td>
                    <const>
                        <constant>-5</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>+10</constant>
                    </const>
                </td>
                <td>3</td>
            </tr>
            <tr>
                <td>
                    <action>BREAK_DRUG</action>
                </td>
                <td>
                    <const>
                        <constant>-5</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>0</constant>
                    </const>
                </td>
                <td>
                    <const>
                        <constant>+10</constant>
                    </const>
                </td>
                <td>3</td>
            </tr>
            "

    "
        </table>
        <h2>Final word!</h2>
        <p><b>Give your feedback and vote to encourage the author! Enjoy your games!</b></p>
    </div>
</div>
"

if($DEBUG){
        "</body>
</html>
"
}