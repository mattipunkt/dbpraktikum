import { Badge } from "~/components/ui/badge";
import {
  Card,
  CardAction,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "../ui/card";

function ProductCard({
  name,
  product_type,
  rating,
  img,
  id,
}: {
  id: number;
  name: string;
  product_type?: string;
  img?: string;
  rating: number;
}) {
  return (
    <Card className="w-full shadow-xl hover:shadow-2xl transition">
      <CardHeader>
        <img
          src={img}
          alt={name}
          width={150}
          height={150}
          className="mx-auto mb-3 rounded-2xl shadow-lg "
        />
        <Badge className="absolute -top-2 right-20 bg-blue-500 text-white">
          {product_type}
        </Badge>
        <CardTitle>{name}</CardTitle>
      </CardHeader>
      <CardFooter className="mt-auto y-0">
        <a href={"/product/" + id} className="w-full">
          <CardAction className="w-full text-center border border-grey text-grey rounded-lg py-2 hover:bg-blue-600 hover:text-white transition">
            Mehr ansehen
          </CardAction>
        </a>
      </CardFooter>
    </Card>
  );
}
export { ProductCard };
