import {
  NavigationMenu,
  NavigationMenuContent,
  NavigationMenuIndicator,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  NavigationMenuTrigger,
  NavigationMenuViewport,
} from "../ui/navigation-menu"
import { PiAlienDuotone, PiYarnLight } from "react-icons/pi"
import { Input } from "../ui/input"

function NavBar() {
    return <div>
      <NavigationMenu>
        <NavigationMenuList>
          <NavigationMenuItem>
            <NavigationMenuLink href="/products" className="flex items-center font-bold text-lg">
              <PiYarnLight size={24}></PiYarnLight>
              Produkte
            </NavigationMenuLink>
          </NavigationMenuItem>
          <NavigationMenuItem>
            <NavigationMenuLink href="/trolls" className="flex items-center font-bold text-lg">
              <PiAlienDuotone size={24}></PiAlienDuotone>
              Trolls
            </NavigationMenuLink>
          </NavigationMenuItem>
          <NavigationMenuItem className="ml-4">
            <Input type="text" placeholder="Suche nach Produkten"/>
          </NavigationMenuItem>

        </NavigationMenuList>
      </NavigationMenu>
    </div>
}

export {
  NavBar
}