#   Get all the necessary values from the Claustrophobia power
execute store result score #stacks neo-origins run data get kaya:power @s neo-origins:claustrophobia@storage stacks

execute store result score #min_stacks neo-origins run data get kaya:power @s neo-origins:claustrophobia@storage min_stacks

execute store result score #max_stacks neo-origins run data get kaya:power @s neo-origins:claustrophobia@storage max_stacks


#   Determine whether the entity is claustrophobic and whether to add/remove to/from the current stack count
execute store success score #should_add_stacks neo-origins if kaya:condition neo-origins:entity/is_claustrophobic with {this: {type: "entity", value: "command/entity"}}


#   If the entity is considered claustrophobic, add onto the current stack count. Otherwise, remove from it
execute if score #should_add_stacks neo-origins matches 1 if score #stacks neo-origins < #max_stacks neo-origins run scoreboard players add #stacks neo-origins 1

execute if score #should_add_stacks neo-origins matches 0 if score #stacks neo-origins > #min_stacks neo-origins run scoreboard players remove #stacks neo-origins 1


#   Store the newly computed stack count to the Claustrophobia power
execute store result kaya:power @s neo-origins:claustrophobia@storage stacks int 1 run scoreboard players get #stacks neo-origins