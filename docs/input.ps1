$header = @{
 "Content-Type" = "application/json"
 "Accept" = "application/json"
} 

$action_url = "http://192.168.20.102/api/action"

$actions = @(
   @{
      actionId = 1
      accountId = 1
      userName = "barry.allen"
      titleId = 1
      userAction = "PlayButtonPress"
      timestamp = "2022-01-12T13:34:00Z"
      pointOfInteraction = "2.59"
      typeOfInteraction = "Play"
   },
   @{
      actionId = 2
      accountId = 1
      userName = "barry.allen"
      titleId = 1
      userAction = "PauseButtonPress"
      timestamp = "2022-01-12T13:35:00Z"
      pointOfInteraction = "3.02"
      typeOfInteraction = "Pause"
   }
)

foreach ( $action in $actions )
{
    $action_json = $action | ConvertTo-Json

    Write-Output "Sending action $($action.userAction) ($($action.timestamp)) for $($action.userName)"
    Invoke-RestMethod -Uri $action_url -Method 'Post' -Body $action_json -Headers $header
}