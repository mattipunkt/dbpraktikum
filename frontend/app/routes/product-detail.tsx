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
import Sellers from "~/components/sellers/sellers";
import Ratings from "~/components/ratings/ratings";
import SimilarProducts from "~/components/similarproducts/similarproducts";

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

        <div className="flex gap-5">
          <i>
            {data.asin}
            {data.isbn && (
              <span>
                , ISBN: <b>{data.isbn}</b>
              </span>
            )}
          </i>{" "}
          <div className="text-yellow-500">
            {"★".repeat(Math.floor(data.rating))}
            {"☆".repeat(Math.ceil(5 - data.rating))}({data.rating})
          </div>
        </div>

        <KategorieListe kategorien={data.kategories} />

        {
          /* angebote */
          <Sellers verkaeufe={data.verkauefe} />
        }
        <Collapsible className="my-4">
          <div className="flex items-center">
            <h2 className="text-xl font-bold mb-2">
              Zusätzliche Informationen
            </h2>
            <CollapsibleTrigger className="flex items-center">
              <a href="#" className="mb-2 ml-5">
                <Badge>Ausklappen</Badge>
              </a>
            </CollapsibleTrigger>
          </div>

          <CollapsibleContent className="border px-4 py-2 shadow-xl rounded-2xl transition">
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
                      <CollapsibleTrigger className="flex items-center">
                        <a href="#" className="mb-2 ml-5">
                          <Badge>Ausklappen</Badge>
                        </a>
                      </CollapsibleTrigger>
                    </div>

                    <CollapsibleContent className="transition">
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
            {
              /* label */
              data.labels && data.labels.length > 0 && (
                <div className="mb-4 flex items-center">
                  <h2 className="text-xl font-semibold mb-2 mr-2">Labels</h2>
                  {data.labels.map((label: any) => (
                    <Badge key={label.id} className="mr-2 mb-2">
                      {label.name}
                    </Badge>
                  ))}
                </div>
              )
            }
            {
              /* label */
              data.verlags && data.verlags.length > 0 && (
                <div className="mb-4 flex items-center">
                  <h2 className="text-xl font-semibold mb-2 mr-2">Verläge</h2>
                  {data.verlags.map((verlags: any) => (
                    <p key={verlags.id} className="mr-2 mb-2">
                      {verlags.name}
                    </p>
                  ))}
                </div>
              )
            }
            {
              /* erscheinungsdatum */
              data.erscheinungsdatum && (
                <div className="mb-4 flex gap-2 items-center">
                  <h2 className="text-xl font-semibold">Erscheinungsdatum</h2>
                  <p>{new Date(data.erscheinungsdatum).toLocaleDateString()}</p>
                </div>
              )
            }
            {
              /* erscheinungsdatum */
              data.seitenzahl && data.seitenzahl > -1 && (
                <div className="mb-4 flex gap-2 items-center">
                  <h2 className="text-xl font-semibold">Seitenzahl</h2>
                  <p>{data.seitenzahl}</p>
                </div>
              )
            }
            {
              /* erscheinungsdatum */
              data.format && (
                <div className="mb-4 flex gap-2 items-center">
                  <h2 className="text-xl font-semibold">Format</h2>
                  <p>{data.format}</p>
                </div>
              )
            }
            {
              /* erscheinungsdatum */
              data.regionCode && (
                <div className="mb-4 flex gap-2 items-center">
                  <h2 className="text-xl font-semibold">Region-Code</h2>
                  <p>{data.regionCode}</p>
                </div>
              )
            }
            {
              /* erscheinungsdatum */
              data.laufzeit && data.laufzeit > -1 && (
                <div className="mb-4 flex gap-2 items-center">
                  <h2 className="text-xl font-semibold">Laufzeit</h2>
                  <p>{data.laufzeit}min</p>
                </div>
              )
            }
          </CollapsibleContent>
        </Collapsible>
        <hr className="my-5" />
        {
          <Ratings ratings={data.bewertungs} productId={data.id} />
          /* rezensionen */
        }
        <hr className="my-5" />
        {
          <SimilarProducts
            products={data.aehnlicheProdukteDto}
          ></SimilarProducts>
        }
      </div>
    </div>
  );
}
