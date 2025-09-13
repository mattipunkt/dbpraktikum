type Filiale = {
  id: number;
  anschrift: string;
  name: string;
};

type Verkauf = {
  filiale: Filiale;
  plz: string;
  preis: number;
  zustand: string;
};

import { Badge } from "~/components/ui/badge";
import {
  Card,
  CardAction,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "~/components/ui/card";

export default function Sellers({ verkaeufe }: { verkaeufe: Verkauf[] }) {
  return (
    <div className="w-full my-4 grid-cols-1 border rounded-2xl px-4 shadow-lg">
      {verkaeufe.map((verkauf) => (
        <div
          className="border-b last:border-0 py-2 flex justify-between items-center"
          key={verkauf.filiale.id}
        >
          <div>
            <div className="text-xl font-semi-bold">{verkauf.filiale.name}</div>
            <div className="text-sm italic">
              {verkauf.filiale.anschrift} {verkauf.plz}
            </div>
          </div>
          <div className="text-right">
            <p>
              {verkauf.preis !== undefined && verkauf.preis !== null
                ? `${verkauf.preis} €`
                : "kein Preis"}
            </p>
            <Badge>{verkauf.zustand}</Badge>
          </div>
        </div>
      ))}
    </div>
  );
}
