# Example tcl script

# Load up the logic tcl extension
load logic

# Fire up our connection to dispatch
dispatchstartup

# Loop through the first thousand point ids in the database
# and set their values to $val
set i 1
set val 0

while { $i < 1000 } {
    SetPoint $i $val
    incr i
}
