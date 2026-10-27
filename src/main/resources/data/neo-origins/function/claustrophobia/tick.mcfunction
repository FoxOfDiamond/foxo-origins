execute store result score #stacks neo-origins run data get neo-apoli:power @s neo-origins:claustrophobia@storage stacks

execute store result score #min_stacks neo-origins run data get neo-apoli:power @s neo-origins:claustrophobia@storage min_stacks

execute store result score #max_stacks neo-origins run data get neo-apoli:power @s neo-origins:claustrophobia@storage max_stacks


execute store success score #should_add_stacks neo-origins if neo-apoli:condition neo-origins:entity/is_claustrophobic with {this: {type: "entity", value: "command/entity"}}


execute if score #should_add_stacks neo-origins matches 1 if score #stacks neo-origins < #max_stacks neo-origins run scoreboard players add #stacks neo-origins 1

execute if score #should_add_stacks neo-origins matches 0 if score #stacks neo-origins > #min_stacks neo-origins run scoreboard players remove #stacks neo-origins 1


execute store result neo-apoli:power @s neo-origins:claustrophobia@storage stacks int 1 run scoreboard players get #stacks neo-origins