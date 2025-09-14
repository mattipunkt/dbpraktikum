import {
  NavigationMenu,
  NavigationMenuContent,
  NavigationMenuIndicator,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  NavigationMenuTrigger,
  NavigationMenuViewport,
} from "../ui/navigation-menu";
import {
  PiAlienDuotone,
  PiListNumbersLight,
  PiMagnifyingGlass,
  PiYarnLight,
} from "react-icons/pi";
import { Input } from "../ui/input";

function NavBar() {
  return (
    <div className="">
      <NavigationMenu>
        <NavigationMenuList>
          <NavigationMenuItem>
            <NavigationMenuLink
              href="/products"
              className="flex items-center font-bold text-lg"
            >
              <PiYarnLight size={24}></PiYarnLight>
              Produkte
            </NavigationMenuLink>
          </NavigationMenuItem>

          <NavigationMenuItem>
            <NavigationMenuLink
              href="/top"
              className="flex items-center font-bold text-lg"
            >
              <PiListNumbersLight size={24}></PiListNumbersLight>
              Top
            </NavigationMenuLink>
          </NavigationMenuItem>

          <NavigationMenuItem>
            <NavigationMenuLink
              href="/trolls"
              className="flex items-center font-bold text-lg"
            >
              <PiAlienDuotone size={24}></PiAlienDuotone>
              Trolls
            </NavigationMenuLink>
          </NavigationMenuItem>
          <NavigationMenuItem>
            <NavigationMenuLink
              href="/search"
              className="flex items-center font-bold text-lg"
            >
              <PiMagnifyingGlass size={24}></PiMagnifyingGlass>
              Suche
            </NavigationMenuLink>
          </NavigationMenuItem>
        </NavigationMenuList>
      </NavigationMenu>
    </div>
  );
}

export { NavBar };
