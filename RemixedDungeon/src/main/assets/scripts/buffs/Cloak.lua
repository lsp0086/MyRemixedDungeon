---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by mike.
--- DateTime: 02.05.19 14:36
---
local RPD  = require "scripts/lib/commonClasses"

local buff = require "scripts/lib/buff"


return buff.init{
    desc  = function ()
        return {
            icon          = 46,
            name          = "CloakBuff_Name",
            info          = "CloakBuff_Info",
        }
    end,

    act = function(self,buff)
        buff:detach()
    end,

    stealthBonus = function(self,buff)
        return buff.target:skillLevel() + buff.target:lvl() / 4
    end,

    charSpriteStatus = function(self, buff)
        return "INVISIBLE"
    end
}