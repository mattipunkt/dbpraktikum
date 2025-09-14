import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "~/components/ui/dialog";
import type { Route } from "./+types/home";
import { useState, useEffect } from "react";
import { Button } from "~/components/ui/button";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Trolls" },
    {
      name: "description",
      content: "product lists shows all your favorite products!",
    },
  ];
}

export default function Trolls() {
  const [k, setK] = useState(2);
  const [data, setData] = useState<any[]>([]);
  useEffect(() => {
    fetch(`http://localhost:8080/trolls?rating=${k}`)
      .then((res) => res.json())
      .then((data) => setData(data));
  }, [k]);
  return (
    <div>
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold mb-4">Trolls</h1>
        <div className="flex items-center gap-2">
          <p>Finde Trolls über: Durchschnitts-Bewertung {"<"}</p>
          <input
            type="number"
            min={0}
            max={5}
            step={0.01}
            className="border p-2 rounded-lg w-20"
            value={k}
            onChange={(e) => setK(Number(e.target.value))}
          ></input>
        </div>
      </div>
      <ul className="list-disc pl-5">
        {data.map((troll) => (
          <Dialog>
            <DialogTrigger asChild>
              <li key={troll.id} className="mb-2">
                <span className="font-medium">{troll.username}</span>
              </li>
            </DialogTrigger>
            <DialogContent className="lg:max-w-screen-lg overflow-y-scroll max-h-screen">
              <DialogHeader>
                <DialogTitle className="text-2xl font-bold">
                  {troll.username}
                </DialogTitle>
                <DialogDescription>
                  Details über {troll.username}
                </DialogDescription>
              </DialogHeader>
              <div className="mt-4">
                <p>
                  <strong>ID:</strong> {troll.id}
                </p>
                {troll.bewertungs.map((bewertung) => (
                  <div
                    key={bewertung.id}
                    className="mt-2 p-2 border rounded-lg"
                  >
                    <p>
                      <strong>Produkt ID:</strong>{" "}
                      <a
                        className="hover:underline hover:font-bold transition"
                        href={`/product/${bewertung.id.produktId}`}
                      >
                        {bewertung.id.produktId}
                      </a>
                    </p>
                    <p>
                      <strong>Rating:</strong> {bewertung.sterne}
                    </p>
                    <p>
                      <strong>Kommentar:</strong> {bewertung.zusammenfassung}
                    </p>
                    <p>{bewertung.rezension}</p>
                  </div>
                ))}
              </div>
            </DialogContent>
          </Dialog>
        ))}
      </ul>
    </div>
  );
}
