export const StitchHomepageEmbed = () => {
  const homepageSrc = "/stitch-homepage.html?v=20260418-08";

  return (
    <main className="h-screen w-full bg-[#f5f7f9]">
      <iframe
        className="h-full w-full border-0"
        loading="eager"
        src={homepageSrc}
        title="Google Stitch Homepage"
      />
    </main>
  );
};
