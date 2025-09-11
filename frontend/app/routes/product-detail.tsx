import type { Route } from "./+types/home";
import { useParams } from "react-router";
import { useEffect, useState } from "react";
import { Badge } from "~/components/ui/badge";
import { Button } from "~/components/ui/button";
import { PiPersonArmsSpreadFill } from "react-icons/pi";
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "~/components/ui/collapsible";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "" },
    {
      name: "description",
      content: "product lists shows all your favorite products!",
    },
  ];
}

function KategorieListe({ kategorien }: { kategorien: any[] }) {
  return (
    <div className="flex flex-wrap gap-2 my-4">
      {kategorien.map((kat) => (
        <a href={"/kategorien/" + kat.id}>
          <Badge
            key={kat}
            className="text-md shadow-md hover:shadow-2xl transition"
          >
            {kat.name}
          </Badge>
        </a>
      ))}
    </div>
  );
}

export default function ProduktDetail() {
  const { productId } = useParams();
  const [data, setData] = useState<any>(null);
  useEffect(() => {
    if (!productId) return;
    fetch("http://localhost:8080/produkt/" + productId)
      .then((res) => res.json())
      .then((data) => setData(data));
  }, [productId]);
  if (!data) return <div>Loading...</div>;
  return (
    <div className="grid grid-cols-4 mt-5">
      <div>
        <img
          src={data.bild}
          alt={"cover picture of " + data.titel}
          className="rounded-xl shadow-xl w-[90%]"
        />
      </div>
      <div className="col-span-3">
        <h1 className="text-3xl font-bold mb-4">{data.titel}</h1>
        <i>{data.asin}</i>
        <KategorieListe kategorien={data.kategories} />
        {
          /* people */
          data.people && data.people.length > 0 && (
            <div className="mb-4">
              <h2 className="text-xl font-semibold mb-2">
                Beteiligte Personen
              </h2>
              <ul className="list-inside">
                {data.people.map((person: any) => (
                  <li key={person.id}>
                    <a
                      href={"/personen/" + person.id}
                      className="text-black-600 hover:underline flex items-center gap-2"
                    >
                      <PiPersonArmsSpreadFill /> {person.name}
                      <small>({person.rolle})</small>
                    </a>
                  </li>
                ))}
              </ul>
            </div>
          )
        }
        {
          /* musiktitel */
          data.musiktitels && data.musiktitels.length > 0 && (
            <div className="mb-4">
              <Collapsible>
                <div className="flex items-center">
                  <h2 className="text-xl font-semibold mb-2">Musiktitel</h2>
                  <CollapsibleTrigger>
                    <a href="#" className="mb-2 ml-5">
                      <Badge>Ausklappen</Badge>
                    </a>
                  </CollapsibleTrigger>
                </div>

                <CollapsibleContent>
                  <ul>
                    {data.musiktitels.map((titel: any) => (
                      <li key={titel.id} className="mb-1">
                        {titel.name}
                      </li>
                    ))}
                  </ul>
                </CollapsibleContent>
              </Collapsible>
            </div>
          )
        }
      </div>
    </div>
  );
}
