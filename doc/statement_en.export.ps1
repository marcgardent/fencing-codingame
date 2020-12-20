param($DEBUG = $False)
$config = "$PSScriptRoot/../config/";

if($DEBUG) {
    copy "$PSScriptRoot/codeingame-mock.css" "$config/level1/"
    copy "$PSScriptRoot/codeingame-mock.css" "$config/level2/"
    copy "$PSScriptRoot/codeingame-mock.css" "$config/level3/"
    copy "$PSScriptRoot/codeingame-mock.css" "$config/"
} else {
    rm "$config/level1/codeingame-mock.css" -ErrorAction SilentlyContinue
    rm "$config/level2/codeingame-mock.css" -ErrorAction SilentlyContinue
    rm "$config/level3/codeingame-mock.css" -ErrorAction SilentlyContinue
    rm "$config/codeingame-mock.css" -ErrorAction SilentlyContinue
}

. "$PSScriptRoot/statement_en.template.ps1" -DEBUG $DEBUG -LEAGUE 1 | Out-File "$config/level1/statement_en.html" -Encoding utf8
. "$PSScriptRoot/statement_en.template.ps1" -DEBUG $DEBUG -LEAGUE 2 | Out-File "$config/level2/statement_en.html" -Encoding utf8
. "$PSScriptRoot/statement_en.template.ps1" -DEBUG $DEBUG -LEAGUE 3 | Out-File "$config/level3/statement_en.html" -Encoding utf8
. "$PSScriptRoot/statement_en.template.ps1" -DEBUG $DEBUG -LEAGUE 4 | Out-File "$config/statement_en.html" -Encoding utf8
